package com.revtalent.employee_service.dto;

import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ManagerProfileResponse {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String designation;
    private String department;
    private String employeeCode;
    private String phone;
    private String gender;
    private LocalDate joiningDate;
    private LocalDate dateOfBirth;
    private String address;
    private String profilePictureUrl;
    private String status;
    private int teamSize;
}