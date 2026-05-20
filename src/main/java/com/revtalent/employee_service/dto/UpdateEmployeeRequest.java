package com.revtalent.employee_service.dto;

import com.revtalent.employee_service.model.Employee;
import lombok.Data;

@Data
public class UpdateEmployeeRequest {
    private String designation;
    private String phone;
    private String address;
    private Long departmentId;
    private String status;

    public Employee.Status parsedStatus() {
        if (status == null || status.isBlank()) return null;
        try {
            return Employee.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }
}
