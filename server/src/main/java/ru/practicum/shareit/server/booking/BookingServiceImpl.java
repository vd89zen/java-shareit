package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;
import ru.practicum.shareit.server.booking.mapper.BookingMapper;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.dto.BookingState;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.item.ItemService;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.WrongOwnerException;
import ru.practicum.shareit.server.exception.WrongRequestException;
import ru.practicum.shareit.server.user.UserService;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private static final String BOOKING_NOT_FOUND = "Заявка с id = %d не найдена.";
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    @Transactional
    public Booking createBooking(Long bookerId, NewBookingDto newBookingDto) {
        log.info("Создание нового бронирования(текущее время {}): {}.", LocalDateTime.now(), newBookingDto);
        LocalDateTime truncatedStart = newBookingDto.getStart().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime truncatedEnd = newBookingDto.getEnd().truncatedTo(ChronoUnit.SECONDS);

        if (truncatedStart.isAfter(truncatedEnd) || truncatedStart.isEqual(truncatedEnd)) {
            throw new WrongRequestException(String.format("Окончание аренды %s не может быть равно или раньше начала аренды %s.",
                            truncatedStart, truncatedEnd));
        }

        User user = userService.getUserById(bookerId);
        Item item = itemService.getItemById(newBookingDto.getItemId());

        if (item.getAvailable() == false) {
            throw new WrongRequestException("Вещь недоступна для аренды");
        }

        Booking newBooking = BookingMapper.toBooking(newBookingDto);
        newBooking.setBooker(user);
        newBooking.setItem(item);
        newBooking.setStatus(Status.WAITING);
        newBooking = bookingRepository.save(newBooking);
        log.info("Создано новое бронирование: {}.", newBooking);
        return newBooking;
    }

    @Override
    @Transactional
    public Booking approveOrRejectBookingById(Long userId, Long bookingId, Boolean approved) {
        log.info("{} Подтверждение/отказ брони ID {} арендодателем {}", approved, bookingId, userId);
        Booking booking = getBookingOrThrow(bookingId);
        final Long ownerId = booking.getItem().getOwner().getId();
        if (userId.equals(ownerId) == false) {
            throw new WrongOwnerException("Пользователь не является владельцем вещи.");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
            booking.setItem(itemService.updateItemAvailable(booking.getItem(), false));
            log.info("{} Подтверждена бронь ID {} арендодателем {}", approved, bookingId, userId);
        } else {
            log.info("{} Отказано в брони ID {} арендодателем {}", approved, bookingId, userId);
            booking.setStatus(Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingByIdWithAccessCheck(Long userId, Long bookingId) {
        log.info("Поиск брони ID {}, с проверкой прав пользователя ID {}", bookingId, userId);
        final Booking booking = getBookingOrThrow(bookingId);
        final Long bookerId = booking.getBooker().getId();
        final Long ownerId = booking.getItem().getOwner().getId();
        if (userId.equals(bookerId) == false && userId.equals(ownerId) == false) {
            throw new WrongOwnerException("Пользователь не является ни создателем брони, ни владельцем вещи");
        }
        log.info("Найдена бронь ID {}, с проверкой прав пользователя ID {}", bookingId, userId);
        return booking;
    }

    @Override
    public List<Booking> getBookingsForBooker(Long bookerId, BookingState state, Integer page, Integer size) {
        log.info("Получение бронирований ({}) пользователя ID {}.", state.name(), bookerId);
        userService.checkUserExists(bookerId);
        Pageable pageable = PageRequest.of(page, size);
        switch (state) {
            case REJECTED:
                return getRejectedBookingsForBooker(bookerId, pageable);
            case WAITING:
                return getWaitingBookingsForBooker(bookerId, pageable);
            case PAST:
                return getPastBookingsForBooker(bookerId, pageable);
            case FUTURE:
                return getFutureBookingsForBooker(bookerId, pageable);
            case CURRENT:
                return getCurrentBookingsForBooker(bookerId, pageable);
            case ALL:
            default:
                return getAllBookingsForBooker(bookerId, pageable);
        }
    }

    @Override
    public List<Booking> getBookingsForOwner(Long ownerId, BookingState state, Integer page, Integer size) {
        log.info("Получение бронирований ({}) для вещей пользователя ID {}", state.name(), ownerId);
        userService.checkUserExists(ownerId);
        Pageable pageable = PageRequest.of(page, size);
        switch (state) {
            case REJECTED:
                return getRejectedBookingsForOwner(ownerId, pageable);
            case WAITING:
                return getWaitingBookingsForOwner(ownerId, pageable);
            case PAST:
                return getPastBookingsForOwner(ownerId, pageable);
            case FUTURE:
                return getFutureBookingsForOwner(ownerId, pageable);
            case CURRENT:
                return getCurrentBookingsForOwner(ownerId, pageable);
            case ALL:
            default:
                return getAllBookingsForOwner(ownerId, pageable);
        }
    }

    @Override
    public void checkBookingExists(Long bookingId) {
        log.info("Проверка существования заявки на аренду ID {}.", bookingId);
        if (bookingRepository.existsById(bookingId) == false) {
            throw new NotFoundException(String.format(BOOKING_NOT_FOUND, bookingId));
        }
        log.info("Заявка на аренду ID {} существует.", bookingId);
    }

    @Override
    public BookingResponseDto getBookingResponseDto(Booking booking) {
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getListBookingResponseDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    private Booking getBookingOrThrow(Long bookingId) {
        log.info("Получение заявки на аренду ID {}.", bookingId);
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(BOOKING_NOT_FOUND, bookingId)));
    }

    //BOOKER
    private List<Booking> getAllBookingsForBooker(Long bookerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageable).getContent();
        log.info("ALL Получены ({}) бронирования пользователя ID {}: {}.", BookingState.ALL.name(), bookerId, bookings);
        return bookings;
    }

    private List<Booking> getRejectedBookingsForBooker(Long bookerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(bookerId, Status.REJECTED, pageable).getContent();
        log.info("REJECTED Получены ({}) бронирования пользователя ID {}: {}.", BookingState.REJECTED.name(), bookerId, bookings);
        return bookings;
    }

    private List<Booking> getWaitingBookingsForBooker(Long bookerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(bookerId, Status.WAITING, pageable).getContent();
        log.info("WAITING Получены ({}) бронирования пользователя ID {}:  {}.", BookingState.WAITING.name(), bookerId, bookings);
        return bookings;
    }

    private List<Booking> getPastBookingsForBooker(Long bookerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByBookerIdPastBookings(
                bookerId, List.of(Status.APPROVED, Status.CANCELED), pageable).getContent();
        log.info("PAST Получены ({}) бронирования пользователя ID {}:  {}.", BookingState.PAST.name(), bookerId, bookings);
        return bookings;
    }

    private List<Booking> getFutureBookingsForBooker(Long bookerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByBookerIdFutureBookings(
                bookerId, List.of(Status.APPROVED, Status.WAITING), pageable).getContent();
        log.info("FUTURE Получены ({}) бронирования пользователя ID {}:  {}.", BookingState.FUTURE.name(), bookerId, bookings);
        return bookings;
    }

    private List<Booking> getCurrentBookingsForBooker(Long bookerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByBookerIdCurrentBookings(bookerId, Status.APPROVED, pageable).getContent();
        log.info("CURRENT Получены ({}) бронирования пользователя ID {}:  {}.", BookingState.CURRENT.name(), bookerId, bookings);
        return bookings;
    }

    //OWNER
    private List<Booking> getAllBookingsForOwner(Long ownerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByOwnerId(ownerId, pageable).getContent();
        log.info("Получены ALL ({}) бронирования пользователя ID {}: {}.", BookingState.ALL.name(), ownerId, bookings);
        return bookings;
    }

    private List<Booking> getRejectedBookingsForOwner(Long ownerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByOwnerIdAndStatus(ownerId, Status.REJECTED, pageable).getContent();
        log.info("Получены REJECTED ({}) бронирования пользователя ID {}: {}.", BookingState.REJECTED.name(), ownerId, bookings);
        return bookings;
    }

    private List<Booking> getWaitingBookingsForOwner(Long ownerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByOwnerIdAndStatus(ownerId, Status.WAITING, pageable).getContent();
        log.info("Получены WAITING ({}) бронирования пользователя ID {}: {}.", BookingState.WAITING.name(), ownerId, bookings);
        return bookings;
    }

    private List<Booking> getPastBookingsForOwner(Long ownerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByOwnerIdPastBookings(
                ownerId, List.of(Status.APPROVED, Status.CANCELED), pageable).getContent();
        log.info("Получены PAST ({}) бронирования пользователя ID {}: {}.", BookingState.PAST.name(), ownerId, bookings);
        return bookings;
    }

    private List<Booking> getFutureBookingsForOwner(Long ownerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByOwnerIdFutureBookings(
                ownerId, List.of(Status.APPROVED, Status.WAITING), pageable).getContent();
        log.info("Получены FUTURE ({}) бронирования пользователя ID {}: {}.", BookingState.FUTURE.name(), ownerId, bookings);
        return bookings;
    }

    private List<Booking> getCurrentBookingsForOwner(Long ownerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByOwnerIdCurrentBookings(ownerId, Status.APPROVED, pageable).getContent();
        log.info("Получены CURRENT ({}) бронирования пользователя ID {}: {}.", BookingState.CURRENT.name(), ownerId, bookings);
        return bookings;
    }

}
