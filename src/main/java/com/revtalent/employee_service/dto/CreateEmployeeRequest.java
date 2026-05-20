package com.revtalent.employee_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateEmployeeRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private String role;
    private String employeeCode;
    private String designation;
    private LocalDate joiningDate;
    private String phone;
    private String address;
    private Long departmentId;
    private String gender;
}
