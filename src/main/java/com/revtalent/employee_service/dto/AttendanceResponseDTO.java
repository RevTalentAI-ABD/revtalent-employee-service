package com.revtalent.employee_service.dto;

import com.revtalent.employee_service.model.Attendance;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceResponseDTO {
    private Long id;
    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer durationMin;
    private String attendanceType;
    private String status;
    private boolean isRegularized;
    private String notes;
    private Long employeeId;
    private String employeeName;

    public static AttendanceResponseDTO from(Attendance a) {
        AttendanceResponseDTO dto = new AttendanceResponseDTO();
        dto.setId(a.getId());
        dto.setWorkDate(a.getWorkDate());
        dto.setCheckIn(a.getCheckIn());
        dto.setCheckOut(a.getCheckOut());
        dto.setDurationMin(a.getDurationMin());
        dto.setAttendanceType(a.getAttendanceType().name());
        dto.setStatus(a.getStatus().name());
        dto.setRegularized(a.isRegularized());
        dto.setNotes(a.getNotes());
        if (a.getEmployee() != null) {
            dto.setEmployeeId(a.getEmployee().getId());
            if (a.getEmployee().getUser() != null) {
                dto.setEmployeeName(a.getEmployee().getUser().getName());
            }
        }
        return dto;
    }
}