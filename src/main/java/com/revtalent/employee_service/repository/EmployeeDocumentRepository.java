package com.revtalent.employee_service.repository;

import com.revtalent.employee_service.model.EmployeeDocument;
import com.revtalent.employee_service.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository




public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {

    List<EmployeeDocument> findByEmployee(Employee employee);
}
