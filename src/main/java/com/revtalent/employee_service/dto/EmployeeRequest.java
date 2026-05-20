package com.revtalent.employee_service.dto.employee;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeRequest {
    private String name;
    private String email;
    private String employeeCode;
    private String designation;
    private LocalDate joiningDate;
    private Long departmentId;
    private UserDTO user;

    @Data
    public static class UserDTO {
        private String name;
        private String username;
        private String email;
        private String password;
        private String role;
    }
}
