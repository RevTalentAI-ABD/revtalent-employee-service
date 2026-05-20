package com.revtalent.employee_service.dto;

import lombok.*;

@Data
public class ProfileUpdateDTO {
    private String name;
    private String email;
    private String phone;
    private String dept;
}
