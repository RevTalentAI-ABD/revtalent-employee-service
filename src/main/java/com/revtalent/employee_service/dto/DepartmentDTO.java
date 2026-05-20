package com.revtalent.employee_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDTO {

    private String name;

    // OPTIONAL
    private Long headEmployeeId;
}
