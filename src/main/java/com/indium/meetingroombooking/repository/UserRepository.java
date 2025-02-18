package com.indium.meetingroombooking.repository;

import com.indium.meetingroombooking.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Short> {
    @Query("SELECT u.userId FROM User u WHERE u.department = 'Administration'")
    List<Long> findAdminUser();
    Optional<User> findByCompanyEmail(String email);


}