package com.revtalent.employee_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignManagerRequest {

    private Long employeeId;

    private Long managerId;
}
