package ru.practicum.shareit.server.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.server.item.dto.ItemBookingDatesDto;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerIdOrderByStartDesc(@Param("bookerId") Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    Page<Booking> findByBookerIdAndStatus(@Param("bookerId") Long bookerId, @Param("status") Status status,
                                          Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "AND b.status IN :statuses " +
            "ORDER BY b.start DESC")
    Page<Booking> findByBookerIdPastBookings(@Param("bookerId") Long bookerId, @Param("statuses") List<Status> statuses,
                                             Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "AND b.status IN :statuses " +
            "ORDER BY b.start DESC")
    Page<Booking> findByBookerIdFutureBookings(@Param("bookerId") Long bookerId, @Param("statuses") List<Status> statuses,
                                               Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    Page<Booking> findByBookerIdCurrentBookings(@Param("bookerId") Long bookerId, @Param("status") Status status,
                                                Pageable pageable);

    //OWNER
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    Page<Booking> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") Status status,
                                         Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "AND b.status IN :statuses " +
            "ORDER BY b.start DESC")
    Page<Booking> findByOwnerIdPastBookings(@Param("ownerId") Long ownerId, @Param("statuses") List<Status> statuses,
                                            Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "AND b.status IN :statuses " +
            "ORDER BY b.start DESC")
    Page<Booking> findByOwnerIdFutureBookings(@Param("ownerId") Long ownerId, @Param("statuses") List<Status> statuses,
                                              Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    Page<Booking> findByOwnerIdCurrentBookings(@Param("ownerId") Long ownerId, @Param("status") Status status,
                                               Pageable pageable);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :userId " +
            "AND b.end < :now " +
            "AND b.status = :status")
    boolean checkBookingItemForComment(@Param("itemId") Long itemId, @Param("userId") Long userId,
                                       @Param("now") LocalDateTime now, @Param("status") Status status);

    @Query("SELECT NEW ru.practicum.shareit.server.item.dto.ItemBookingDatesDto(" +
            "b.item.id, " +
            "MAX(CASE WHEN b.end < CURRENT_TIMESTAMP THEN b.start ELSE NULL END), " +
            "MIN(CASE WHEN b.start > CURRENT_TIMESTAMP THEN b.start ELSE NULL END)) " +
            "FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "GROUP BY b.item.id")
    List<ItemBookingDatesDto> findBookingDatesByItemIds(@Param("itemIds") List<Long> itemIds);

    @Query("SELECT NEW ru.practicum.shareit.server.item.dto.ItemBookingDatesDto(" +
            "b.item.id, " +
            "MAX(CASE WHEN b.end < CURRENT_TIMESTAMP THEN b.start ELSE NULL END), " +
            "MIN(CASE WHEN b.start > CURRENT_TIMESTAMP THEN b.start ELSE NULL END)) " +
            "FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "GROUP BY b.item.id")
    ItemBookingDatesDto findBookingDatesByItemId(@Param("itemId") Long itemId);

}
