package com.indium.meetingroombooking.repository;

import com.indium.meetingroombooking.entity.AuditLogs;
import org.springframework.data.repository.CrudRepository;

public interface AuditLogsRepository extends CrudRepository<AuditLogs,Short> {
}
