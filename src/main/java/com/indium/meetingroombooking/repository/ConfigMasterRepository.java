package com.indium.meetingroombooking.repository;

import com.indium.meetingroombooking.entity.ConfigMaster;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
public interface ConfigMasterRepository extends CrudRepository<ConfigMaster,Integer> {
    // Query to check if a config record exists with a specific email in the JSON column
//    @Query(value = "SELECT JSON_UNQUOTE(JSON_EXTRACT(c.config_employee_code, '$[*].email')) FROM config_master c", nativeQuery = true)
//    List<String> getEmailsFromConfigMaster();
//    @Query(value = "SELECT JSON_UNQUOTE(JSON_EXTRACT(c.config_employee_code, CONCAT('$[', i.index, '].email'))) AS email " +
//            "FROM config_master c " +
//            "JOIN (SELECT 0 AS index UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3) i " +
//            "ON JSON_LENGTH(c.config_employee_code) > i.index", nativeQuery = true)
//    List<String> getEmailsFromConfigMaster();
//    @Query(value = "SELECT JSON_UNQUOTE(JSON_EXTRACT(c.config_employee_code, '$[*].email')) " +
//            "FROM config_master c", nativeQuery = true)
//    List<String> getEmailsFromConfigMaster();
//    @Query(value = "SELECT JSON_UNQUOTE(JSON_EXTRACT(c.config_employee_code, CONCAT('$[', n.n, '].email'))) " +
//            "FROM config_master c, " +
//            "(SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3) n " +
//            "WHERE JSON_LENGTH(c.config_employee_code) > n.n", nativeQuery = true)
//    List<String> getEmailsFromConfigMaster();
    @Query(value = "SELECT JSON_UNQUOTE(JSON_EXTRACT(c.config_employee_code, CONCAT('$[', n.n, ']'))) " +
            "FROM config_master c, " +
            "(SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3) n " +
            "WHERE JSON_LENGTH(c.config_employee_code) > n.n", nativeQuery = true)
    List<String> getUserIdsFromConfigMaster();

}
