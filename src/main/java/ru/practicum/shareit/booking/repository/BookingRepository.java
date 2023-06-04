package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.booker as bb " +
            "WHERE bb.id = ?1 AND (b.start <= ?2 AND b.end >= ?2) " +
            "ORDER BY b.start DESC")
    public Collection<Booking> getAllCurrentBookingsByBookerId(long bookerId, LocalDateTime rightMoment);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.item as i " +
            "JOIN i.owner as o " +
            "WHERE o.id = ?1 AND (b.start <= ?2 AND b.end >= ?2) " +
            "ORDER BY b.start DESC")
    public Collection<Booking> getAllCurrentBookingsByOwnerId(long ownerId, LocalDateTime rightMoment);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.booker as bb " +
            "WHERE bb.id = ?1 AND (b.end > ?2)  " +
            "ORDER BY b.start DESC")
    public Collection<Booking> getAllFutureBookingsByBookerId(long bookerId, LocalDateTime rightMoment);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.item as i " +
            "JOIN i.owner as o " +
            "WHERE o.id = ?1 AND (b.end > ?2) " +
            "ORDER BY b.start DESC")
    public Collection<Booking> getAllFutureBookingsByOwnerId(long ownerId, LocalDateTime rightMoment);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.booker as bb " +
            "WHERE bb.id = ?1 AND (b.end < ?2) " +
            "ORDER BY b.start DESC")
    public Collection<Booking> getAllPastBookingsByBookerId(long bookerId, LocalDateTime rightMoment);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.item as i " +
            "JOIN i.owner as o " +
            "WHERE o.id = ?1 AND (b.end < ?2) " +
            "ORDER BY b.start DESC")
    public Collection<Booking> getAllPastBookingsByOwnerId(long ownerId, LocalDateTime rightMoment);

    public Collection<Booking> getAllByBookerIdAndStateOrderByStartDesc(long bookerId, BookingState state);

    public Collection<Booking> getAllByItemOwnerIdAndStateOrderByStartDesc(long ownerId, BookingState state);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.item as i " +
            "JOIN i.owner as o " +
            "WHERE o.id = ?1 " +
            "ORDER BY b.start DESC")
    public Collection<Booking> getBookingListByOwnerId(long ownerId);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.booker as bb " +
            "WHERE bb.id = ?1 " +
            "ORDER BY b.start DESC")
    public Collection<Booking> getBookingListByBookerId(long bookerId);


    public Booking findFirstBookingByItemIdAndStartIsBeforeAndStateNotLikeOrderByStartDesc(
            long itemId, LocalDateTime dateTime, BookingState state);

    public Booking findFirstBookingByItemIdAndEndIsBeforeAndStateNotLikeOrderByEndDesc(
            long itemId, LocalDateTime dateTime, BookingState state);

    public Booking findFirstBookingByItemIdAndStartIsAfterAndStateNotLikeOrderByStartAsc(
            long itemId, LocalDateTime dateTime, BookingState state);
}
