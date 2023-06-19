package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.booker as bb " +
            "WHERE bb.id = ?1 AND (b.start <= ?2 AND b.end >= ?2) " +
            "ORDER BY b.start DESC")
    public List<Booking> getAllCurrentBookingsByBookerId(long bookerId, LocalDateTime rightMoment, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.item as i " +
            "JOIN i.owner as o " +
            "WHERE o.id = ?1 AND (b.start <= ?2 AND b.end >= ?2) " +
            "ORDER BY b.start DESC")
    public List<Booking> getAllCurrentBookingsByOwnerId(long ownerId, LocalDateTime rightMoment, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.booker as bb " +
            "WHERE bb.id = ?1 AND (b.end > ?2)  " +
            "ORDER BY b.start DESC")
    public List<Booking> getAllFutureBookingsByBookerId(long bookerId, LocalDateTime rightMoment, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.item as i " +
            "JOIN i.owner as o " +
            "WHERE o.id = ?1 AND (b.end > ?2) " +
            "ORDER BY b.start DESC")
    public List<Booking> getAllFutureBookingsByOwnerId(long ownerId, LocalDateTime rightMoment, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.booker as bb " +
            "WHERE bb.id = ?1 AND (b.end < ?2) " +
            "ORDER BY b.start DESC")
    public List<Booking> getAllPastBookingsByBookerId(long bookerId, LocalDateTime rightMoment, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.item as i " +
            "JOIN i.owner as o " +
            "WHERE o.id = ?1 AND (b.end < ?2) " +
            "ORDER BY b.start DESC")
    public List<Booking> getAllPastBookingsByOwnerId(long ownerId, LocalDateTime rightMoment, Pageable pageable);

    public List<Booking> getAllByBookerIdAndStateOrderByStartDesc(long bookerId, BookingState state,
                                                                  Pageable pageable);

    public List<Booking> getAllByItemOwnerIdAndStateOrderByStartDesc(long ownerId, BookingState state,
                                                                     Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.item as i " +
            "JOIN i.owner as o " +
            "WHERE o.id = ?1 " +
            "ORDER BY b.start DESC")
    public List<Booking> getBookingListByOwnerId(long ownerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.booker as bb " +
            "WHERE bb.id = ?1 " +
            "ORDER BY b.start DESC")
    public List<Booking> getBookingListByBookerId(long bookerId, Pageable pageable);


    public Booking findFirstBookingByItemIdAndStartIsBeforeAndStateNotLikeOrderByStartDesc(
            long itemId, LocalDateTime dateTime, BookingState state);

    public Booking findFirstBookingByItemIdAndEndIsBeforeAndStateNotLikeOrderByEndDesc(
            long itemId, LocalDateTime dateTime, BookingState state);

    public Booking findFirstBookingByItemIdAndStartIsAfterAndStateNotLikeOrderByStartAsc(
            long itemId, LocalDateTime dateTime, BookingState state);
}
