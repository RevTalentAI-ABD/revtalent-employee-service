package com.revtalent.employee_service.dto;

import lombok.Data;

@Data
public class PatchEmployeeRequest {
    private String phone;
    private String address;
    private String designation;
    private String profilePictureUrl;
    private String name;
    private String email;
}