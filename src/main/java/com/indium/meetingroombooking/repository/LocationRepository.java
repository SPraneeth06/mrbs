package com.indium.meetingroombooking.repository;

import com.indium.meetingroombooking.entity.LocationDetails;
import org.springframework.data.repository.CrudRepository;

public interface LocationRepository extends CrudRepository<LocationDetails,Short> {
    boolean existsByLocationNameIgnoreCase(String locationName);

}
