package com.revtalent.employee_service.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class EmployeeResponse {
    private Long id;
    private String employeeCode;
    private String designation;
    private String status;
    private String departmentName;
    private Long departmentId;
    private Long managerId;
    private String managerName;
    private String name;
    private String username;
    private String email;
    private String role;
    private String phone;
    private String address;
    private String profilePictureUrl;
    private LocalDate joiningDate;
    private LocalDate dateOfBirth;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
