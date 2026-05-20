package com.revtalent.employee_service.dto;

import lombok.Data;

@Data
public class EmployeeCreateDTO {

    private String firstName;
    private String lastName;
    private String email;

    private Long departmentId;

    private String designation;
    private String phone;

    private String password;
}
