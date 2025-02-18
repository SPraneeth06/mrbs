package com.indium.meetingroombooking.repository;

import com.indium.meetingroombooking.entity.LocationDetails;
import com.indium.meetingroombooking.entity.RoomDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends CrudRepository<RoomDetails,Short> {
    List<RoomDetails> findByLocationIdAndIsActive(LocationDetails locationDetails, Character isActive);
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM RoomDetails r WHERE r.roomName = :roomName AND r.locationId.locationId = :locationId")
    boolean existsByRoomNameAndLocationId(@Param("roomName") String roomName, @Param("locationId") Short locationId);
}
