package com.indium.meetingroombooking.repository;

import com.indium.meetingroombooking.entity.Booking;
import com.indium.meetingroombooking.entity.LocationDetails;
import com.indium.meetingroombooking.entity.RoomDetails;
import com.indium.meetingroombooking.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Short> {
    List<Booking> findByUserId(User user);

    List<Booking> findByRoom(RoomDetails room);

    List<Booking> findByLocation(LocationDetails location);
    // ✅ Check if new booking overlaps an existing one
    @Query("""
        SELECT COUNT(b) > 0 
        FROM Booking b 
        WHERE b.room = :room 
        AND (:startTime BETWEEN b.startTime AND b.endTime 
             OR :endTime BETWEEN b.startTime AND b.endTime)
    """)
    boolean existsOverlapBooking(@Param("room") RoomDetails room,
                                 @Param("startTime") LocalDateTime startTime,
                                 @Param("endTime") LocalDateTime endTime);

    // ✅ Check if the booking is within an existing one
    @Query("""
        SELECT COUNT(b) > 0 
        FROM Booking b 
        WHERE b.room = :room 
        AND (b.startTime < :endTime AND b.endTime > :startTime)
    """)
    boolean existsBookingWithinInterval(@Param("room") RoomDetails room,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    // ✅ **Fix Buffer Time Query**
    @Query("""
    SELECT COUNT(b) > 0 
    FROM Booking b 
    WHERE b.room = :room 
    AND (
        b.endTime >= :bufferStartTime 
        AND b.startTime <= :endTime
        OR b.startTime <= :bufferEndTime 
        AND b.endTime >= :startTime
    )
""")
    boolean existsBufferTimeConflict(@Param("room") RoomDetails room,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     @Param("bufferStartTime") LocalDateTime bufferStartTime,
                                     @Param("bufferEndTime") LocalDateTime bufferEndTime);

}




//    // Check if any booking conflicts with the given time range
//    boolean existsByRoomAndStartTimeBetween(RoomDetails room, LocalDateTime startTime, LocalDateTime endTime);
//
//    boolean existsByRoomAndEndTimeBetween(RoomDetails room, LocalDateTime startTime, LocalDateTime endTime);
//
//    boolean existsByRoomAndStartTimeLessThanAndEndTimeGreaterThan(RoomDetails room, LocalDateTime startTime, LocalDateTime endTime);
//
//    // Fetch all bookings for a specific room within a date range
//    @Query("SELECT b FROM Booking b WHERE b.room = :room AND b.startTime >= :startTime AND b.endTime <= :endTime")
//    List<Booking> findBookingsForRoomWithinTimeRange(RoomDetails room, LocalDateTime startTime, LocalDateTime endTime);
//
//    // Count recurring bookings for a user
//    @Query("SELECT COUNT(b) FROM Booking b WHERE b.userId.userId = :userId AND b.isRecurring = 'y'")
//    int countRecurringBookingsByUser(Long userId);
//    // **Fix: Single query to check for full booking conflicts**
//    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room = :room AND " +
//            "((b.startTime BETWEEN :bufferStartTime AND :bufferEndTime) " +
//            "OR (b.endTime BETWEEN :bufferStartTime AND :bufferEndTime) " +
//            "OR (:bufferStartTime BETWEEN b.startTime AND b.endTime) " +
//            "OR (:bufferEndTime BETWEEN b.startTime AND b.endTime))")
//    boolean isRoomBookedDuringPeriod(@Param("room") RoomDetails room,
//                                     @Param("bufferStartTime") LocalDateTime bufferStartTime,
//                                     @Param("bufferEndTime") LocalDateTime bufferEndTime);





