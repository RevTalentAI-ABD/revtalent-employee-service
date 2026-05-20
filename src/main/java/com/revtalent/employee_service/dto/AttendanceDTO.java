package com.revtalent.employee_service.dto;

import com.revtalent.employee_service.model.Attendance;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceDTO {
    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Attendance.AttendanceType attendanceType;
    private Attendance.Status status;
    private String notes;
}