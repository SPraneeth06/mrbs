package com.indium.meetingroombooking.repository;

import com.indium.meetingroombooking.entity.EmailCommunicationDetails;
import org.springframework.data.repository.CrudRepository;

public interface EmailCommunicationRepository extends CrudRepository<EmailCommunicationDetails,Integer> {
}
