package com.revtalent.employee_service.controller;

import com.revtalent.employee_service.dto.DepartmentDTO;
import com.revtalent.employee_service.model.Department;
import com.revtalent.employee_service.model.Employee;
import com.revtalent.employee_service.repository.DepartmentRepository;
import com.revtalent.employee_service.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    // CREATE DEPARTMENT
    @PostMapping
    public Department createDepartment(
            @RequestBody DepartmentDTO dto
    ) {

        Department department = new Department();

        department.setName(dto.getName());

        // OPTIONAL HEAD
        if (dto.getHeadEmployeeId() != null) {

            Employee employee = employeeRepository
                    .findById(dto.getHeadEmployeeId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Employee not found with id: "
                                            + dto.getHeadEmployeeId()
                            )
                    );

            department.setHead(employee);
        }

        return departmentRepository.save(department);
    }

    // GET ALL DEPARTMENTS
    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    // GET DEPARTMENT BY ID
    @GetMapping("/{id}")
    public Department getDepartmentById(
            @PathVariable Long id
    ) {
        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Department not found with id: " + id
                        )
                );
    }

    // UPDATE DEPARTMENT HEAD
    @PutMapping("/{departmentId}/head/{employeeId}")
    public Department assignDepartmentHead(
            @PathVariable Long departmentId,
            @PathVariable Long employeeId
    ) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Department not found"
                        )
                );

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found"
                        )
                );

        department.setHead(employee);

        return departmentRepository.save(department);
    }
}
