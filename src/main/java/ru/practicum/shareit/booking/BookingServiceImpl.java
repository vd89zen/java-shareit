package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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
    public Booking create(Long bookerId, BookingDto bookingDto) {
        log.info("Создание нового бронирования(текущее время {}): {}.", LocalDateTime.now(), bookingDto);
        LocalDateTime truncatedStart = bookingDto.getStart().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime truncatedEnd = bookingDto.getEnd().truncatedTo(ChronoUnit.SECONDS);

        if (truncatedStart.isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))) {
            throw new WrongRequestException(String.format("Начало аренды %s не может быть раньше текущей даты.",
                            truncatedStart));
        }

        if (truncatedStart.isAfter(truncatedEnd) || truncatedStart.isEqual(truncatedEnd)) {
            throw new WrongRequestException(String.format("Окончание аренды %s не может быть равно или раньше начала аренды %s.",
                            truncatedStart, truncatedEnd));
        }

        User user = userService.findById(bookerId);
        Item item = itemService.findById(bookingDto.getItemId());

        if (item.getAvailable() == false) {
            throw new WrongRequestException("Вещь недоступна для аренды");
        }

        Booking newBooking = BookingMapper.toBooking(bookingDto);
        newBooking.setBooker(user);
        newBooking.setItem(item);
        newBooking.setStatus(Status.WAITING);
        newBooking = bookingRepository.save(newBooking);
        log.info("Создано новое бронирование: {}.", newBooking);
        return newBooking;
    }

    @Override
    @Transactional
    public Booking approveOrRejectBooking(Long userId, Long bookingId, Boolean approved) {
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

    //ВОЗМОЖНО НАДО БУДЕТ ПОМЕНЯТЬ ЭКЦЕПШН
    @Override
    public Booking getBookingWithAccessCheck(Long userId, Long bookingId) {
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
    public List<Booking> getBookingsForBooker(Long bookerId, State state) {
        log.info("Получение бронирований ({}) пользователя ID {}.", state.name(), bookerId);
        userService.checkUserExists(bookerId);
        switch (state) {
            case ALL:
                return getAllBookingsForBooker(bookerId);
            case REJECTED:
                return getRejectedBookingsForBooker(bookerId);
            case WAITING:
                return getWaitingBookingsForBooker(bookerId);
            case PAST:
                return getPastBookingsForBooker(bookerId);
            case FUTURE:
                return getFutureBookingsForBooker(bookerId);
            case CURRENT:
                return getCurrentBookingsForBooker(bookerId);
            default:
                throw new ValidationException(ValidationError.builder()
                        .field("Эндпоинт: GET /bookings?state={state}")
                        .message(String.format("Неверный параметр state, допустимые варианты: %s",
                                Arrays.toString(State.values())))
                        .rejectedValue(state.name())
                        .build());
        }
    }

    @Override
    public List<Booking> getBookingsForOwner(Long ownerId, State state) {
        log.info("Получение бронирований ({}) для вещей пользователя ID {}", state.name(), ownerId);
        userService.checkUserExists(ownerId);
        switch (state) {
            case ALL:
                return getAllBookingsForOwner(ownerId);
            case REJECTED:
                return getRejectedBookingsForOwner(ownerId);
            case WAITING:
                return getWaitingBookingsForOwner(ownerId);
            case PAST:
                return getPastBookingsForOwner(ownerId);
            case FUTURE:
                return getFutureBookingsForOwner(ownerId);
            case CURRENT:
                return getCurrentBookingsForOwner(ownerId);
            default:
                throw new ValidationException(ValidationError.builder()
                        .field("Эндпоинт: GET /bookings/owner?state={state}")
                        .message(String.format("Неверный параметр state, допустимые варианты: %s",
                                Arrays.toString(State.values())))
                        .rejectedValue(state.name())
                        .build());
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

    private List<Booking> getAllBookingsForBooker(Long bookerId) {
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
        log.info("ALL Получены ({}) бронирования пользователя ID {}: {}.", State.ALL.name(), bookerId, bookings);
        return bookings;
    }

    private List<Booking> getRejectedBookingsForBooker(Long bookerId) {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(bookerId, Status.REJECTED);
        log.info("REJECTED Получены ({}) бронирования пользователя ID {}: {}.", State.REJECTED.name(), bookerId, bookings);
        return bookings;
    }

    private List<Booking> getWaitingBookingsForBooker(Long bookerId) {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(bookerId, Status.WAITING);
        log.info("WAITING Получены ({}) бронирования пользователя ID {}:  {}.", State.WAITING.name(), bookerId, bookings);
        return bookings;
    }

    private List<Booking> getPastBookingsForBooker(Long bookerId) {
        List<Booking> bookings = bookingRepository.findByBookerIdPastBookings(bookerId, List.of(Status.APPROVED, Status.CANCELED));
        log.info("PAST Получены ({}) бронирования пользователя ID {}:  {}.", State.PAST.name(), bookerId, bookings);
        return bookings;
    }

    private List<Booking> getFutureBookingsForBooker(Long bookerId) {
        List<Booking> bookings = bookingRepository.findByBookerIdFutureBookings(bookerId, List.of(Status.APPROVED, Status.WAITING));
        log.info("FUTURE Получены ({}) бронирования пользователя ID {}:  {}.", State.FUTURE.name(), bookerId, bookings);
        return bookings;
    }

    private List<Booking> getCurrentBookingsForBooker(Long bookerId) {
        List<Booking> bookings = bookingRepository.findByBookerIdCurrentBookings(bookerId, Status.APPROVED);
        log.info("CURRENT Получены ({}) бронирования пользователя ID {}:  {}.", State.CURRENT.name(), bookerId, bookings);
        return bookings;
    }

    private List<Booking> getAllBookingsForOwner(Long ownerId) {
        List<Booking> bookings = bookingRepository.findByOwnerId(ownerId);
        log.info("Получены ALL ({}) бронирования пользователя ID {}: {}.", State.ALL.name(), ownerId, bookings);
        return bookings;
    }

    private List<Booking> getRejectedBookingsForOwner(Long ownerId) {
        List<Booking> bookings = bookingRepository.findByOwnerIdAndStatus(ownerId, Status.REJECTED);
        log.info("Получены REJECTED ({}) бронирования пользователя ID {}: {}.", State.REJECTED.name(), ownerId, bookings);
        return bookings;
    }

    private List<Booking> getWaitingBookingsForOwner(Long ownerId) {
        List<Booking> bookings = bookingRepository.findByOwnerIdAndStatus(ownerId, Status.WAITING);
        log.info("Получены WAITING ({}) бронирования пользователя ID {}: {}.", State.WAITING.name(), ownerId, bookings);
        return bookings;
    }

    private List<Booking> getPastBookingsForOwner(Long ownerId) {
        List<Booking> bookings = bookingRepository.findByOwnerIdPastBookings(ownerId, List.of(Status.APPROVED, Status.CANCELED));
        log.info("Получены PAST ({}) бронирования пользователя ID {}: {}.", State.PAST.name(), ownerId, bookings);
        return bookings;
    }

    private List<Booking> getFutureBookingsForOwner(Long ownerId) {
        List<Booking> bookings = bookingRepository.findByOwnerIdFutureBookings(ownerId, List.of(Status.APPROVED, Status.WAITING));
        log.info("Получены FUTURE ({}) бронирования пользователя ID {}: {}.", State.FUTURE.name(), ownerId, bookings);
        return bookings;
    }

    private List<Booking> getCurrentBookingsForOwner(Long ownerId) {
        List<Booking> bookings = bookingRepository.findByOwnerIdCurrentBookings(ownerId, Status.APPROVED);
        log.info("Получены CURRENT ({}) бронирования пользователя ID {}: {}.", State.CURRENT.name(), ownerId, bookings);
        return bookings;
    }

}
