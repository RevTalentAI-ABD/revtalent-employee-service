package com.revtalent.employee_service.dto;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class AttendanceSummaryResponse {
    private long totalEmployees;
    private long present;
    private long absent;
    private long wfh;
    private long onLeave;
    private long field;
}