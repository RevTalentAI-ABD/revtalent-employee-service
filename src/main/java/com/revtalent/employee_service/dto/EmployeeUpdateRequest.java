package com.revtalent.employee_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeUpdateRequest {
    private String designation;
    private String phone;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
}