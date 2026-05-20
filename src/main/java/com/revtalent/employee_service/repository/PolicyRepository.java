package com.revtalent.employee_service.repository;

import com.revtalent.employee_service.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
}
