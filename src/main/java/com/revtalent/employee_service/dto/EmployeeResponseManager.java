package com.revtalent.employee_service.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseManager {
    private Long id;
    private String name;
    private String designation;
    private String department;
    private String status;
    private String employeeCode;
    private String phone;
}
