package ru.practicum.shareit.server.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.user.mapper.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemResponseDto(booking.getItem()))
                .booker(UserMapper.toUserResponseDto(booking.getBooker()))
                .status(booking.getStatus().name())
                .build();
    }

    public static Booking toBooking(NewBookingDto newBookingDto) {
        return Booking.builder()
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .build();
    }
}
