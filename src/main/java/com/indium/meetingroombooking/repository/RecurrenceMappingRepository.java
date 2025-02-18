package com.indium.meetingroombooking.repository;

import com.indium.meetingroombooking.entity.Booking;
import com.indium.meetingroombooking.entity.RecurrenceMappingDetails;
import com.indium.meetingroombooking.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RecurrenceMappingRepository extends CrudRepository<RecurrenceMappingDetails,Short> {
    // Count how many recurring bookings exist for a user
    // Count how many recurring bookings exist for a user
    int countByBookingUserId(User userId);
    Optional<RecurrenceMappingDetails> findByBooking(Booking booking);

}
