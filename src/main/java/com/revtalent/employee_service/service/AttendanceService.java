package com.revtalent.employee_service.service;

import com.revtalent.employee_service.dto.AttendanceDTO;
import com.revtalent.employee_service.dto.AttendanceResponseDTO;
import com.revtalent.employee_service.dto.AttendanceResponse;
import com.revtalent.employee_service.dto.AttendanceSummaryResponse;
import com.revtalent.employee_service.model.Attendance;
import com.revtalent.employee_service.model.Employee;
import com.revtalent.employee_service.repository.AttendanceRepository;
import com.revtalent.employee_service.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    // ── Helper ──────────────────────────────────────────────────────────────

    private Attendance fetchAttendanceEntity(Long attendanceId) {
        return attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance record not found: " + attendanceId));
    }

    private AttendanceResponse toDTO(Attendance a) {
        AttendanceResponse res = new AttendanceResponse();
        res.setId(a.getId());
        res.setEmployeeId(a.getEmployee() != null ? a.getEmployee().getId() : null);
        res.setEmployeeName(a.getEmployee() != null && a.getEmployee().getUser() != null
                ? a.getEmployee().getUser().getName() : "N/A");
        
        String role = "N/A";
        if (a.getEmployee() != null && a.getEmployee().getUser() != null && a.getEmployee().getUser().getRole() != null) {
            role = String.valueOf(a.getEmployee().getUser().getRole());
            if (role.startsWith("ROLE_")) {
                role = role.substring(5);
            }
        }
        res.setEmployeeRole(role);
        
        res.setEmployeeCode(a.getEmployee() != null ? a.getEmployee().getEmployeeCode() : null);
        res.setDepartment(a.getEmployee() != null && a.getEmployee().getDepartment() != null
                ? a.getEmployee().getDepartment().getName() : "N/A");
        res.setWorkDate(a.getWorkDate());
        res.setCheckIn(a.getCheckIn());
        res.setCheckOut(a.getCheckOut());
        res.setDurationMin(a.getDurationMin());
        res.setAttendanceType(String.valueOf(a.getAttendanceType()));
        res.setStatus(String.valueOf(a.getStatus()));
        res.setRegularized(a.isRegularized());
        res.setNotes(a.getNotes());
        return res;
    }

    // ── Employee methods ────────────────────────────────────────────────────

    public List<AttendanceResponseDTO> getByEmployee(Long empId) {
        return attendanceRepository.findByEmployee_IdOrderByWorkDateDesc(empId)
                .stream()
                .map(AttendanceResponseDTO::from)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponseDTO> getByEmployeeAndDateRange(Long empId, LocalDate from, LocalDate to) {
        return attendanceRepository
                .findByEmployee_IdAndWorkDateBetweenOrderByWorkDateDesc(empId, from, to)
                .stream()
                .map(AttendanceResponseDTO::from)
                .collect(Collectors.toList());
    }

    public AttendanceResponseDTO getByEmployeeAndDate(Long empId, LocalDate date) {
        Attendance a = attendanceRepository.findByEmployee_IdAndWorkDate(empId, date)
                .orElseThrow(() -> new RuntimeException(
                        "No attendance record for employee " + empId + " on " + date));
        return AttendanceResponseDTO.from(a);
    }

    public List<AttendanceResponseDTO> getByDate(LocalDate date) {
        return attendanceRepository.findByWorkDate(date)
                .stream()
                .map(AttendanceResponseDTO::from)
                .collect(Collectors.toList());
    }

    public AttendanceResponseDTO checkIn(Long empId, AttendanceDTO dto) {
        if (attendanceRepository.findByEmployee_IdAndWorkDate(empId, LocalDate.now()).isPresent()) {
            throw new RuntimeException("Already checked in today");
        }

        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + empId));

        Attendance.AttendanceType type = dto.getAttendanceType() != null
                ? dto.getAttendanceType()
                : Attendance.AttendanceType.WFO;

        Attendance attendance = Attendance.builder()
                .employee(emp)
                .workDate(LocalDate.now())
                .checkIn(LocalDateTime.now())
                .attendanceType(type)
                .status(type == Attendance.AttendanceType.WFH ? Attendance.Status.WFH : Attendance.Status.PRESENT)
                .notes(dto.getNotes())
                .build();

        return AttendanceResponseDTO.from(attendanceRepository.save(attendance));
    }

    public AttendanceResponseDTO checkOut(Long empId) {
        Attendance attendance = attendanceRepository
                .findByEmployee_IdAndWorkDate(empId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("No check-in found for today"));

        if (attendance.getCheckOut() != null) {
            throw new RuntimeException("Already checked out today");
        }

        LocalDateTime checkOut = LocalDateTime.now();
        attendance.setCheckOut(checkOut);


        if (attendance.getCheckIn() != null) {
            long minutes = java.time.temporal.ChronoUnit.MINUTES.between(attendance.getCheckIn(), checkOut);
            attendance.setDurationMin((int) minutes);
        }

        return AttendanceResponseDTO.from(attendanceRepository.save(attendance));
    }

    public AttendanceResponseDTO save(Long empId, AttendanceDTO dto) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + empId));

        Attendance attendance = attendanceRepository
                .findByEmployee_IdAndWorkDate(empId, dto.getWorkDate())
                .orElse(new Attendance());

        attendance.setEmployee(emp);
        attendance.setWorkDate(dto.getWorkDate());
        attendance.setCheckIn(dto.getCheckIn());
        attendance.setCheckOut(dto.getCheckOut());
        Attendance.AttendanceType type = dto.getAttendanceType() != null
                ? dto.getAttendanceType()
                : Attendance.AttendanceType.WFO;

        attendance.setAttendanceType(type);
        attendance.setStatus(dto.getStatus() != null
                ? dto.getStatus()
                : (type == Attendance.AttendanceType.WFH ? Attendance.Status.WFH : Attendance.Status.PRESENT));
        attendance.setNotes(dto.getNotes());

        return AttendanceResponseDTO.from(attendanceRepository.save(attendance));
    }

    public AttendanceResponseDTO regularize(Long attendanceId, AttendanceDTO dto) {
        Attendance attendance = fetchAttendanceEntity(attendanceId);
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isHr = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_HR_ADMIN"));
        if (!isHr) {
             Employee loggedInEmp = employeeRepository.findByUser_Username(auth.getName()).orElseThrow(() -> new RuntimeException("Employee not found"));
             if (!attendance.getEmployee().getId().equals(loggedInEmp.getId())) {
                  throw new org.springframework.security.access.AccessDeniedException("Cannot regularize another employee's attendance");
             }
        }
        attendance.setCheckIn(dto.getCheckIn());
        attendance.setCheckOut(dto.getCheckOut());
        attendance.setNotes(dto.getNotes());
        attendance.setRegularized(true);
        return AttendanceResponseDTO.from(attendanceRepository.save(attendance));
    }

    public int getPresentCount(Long empId, LocalDate from, LocalDate to) {
        return attendanceRepository.countByEmployee_IdAndWorkDateBetweenAndStatus(
                empId, from, to, Attendance.Status.PRESENT);
    }

    public void delete(Long attendanceId) {
        if (!attendanceRepository.existsById(attendanceId)) {
            throw new RuntimeException("Attendance record not found: " + attendanceId);
        }
        attendanceRepository.deleteById(attendanceId);
    }

    // ── Manager methods ─────────────────────────────────────────────────────

    public List<AttendanceResponse> getAttendance() {
        return attendanceRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // HR — shows everything, no filter
    public List<AttendanceResponse> getAll() {
        return attendanceRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponse> getOwnAttendance(String username) {
        Employee emp = employeeRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return attendanceRepository.findByEmployee_IdOrderByWorkDateDesc(emp.getId())
                .stream()
                .map(a -> {
                    AttendanceResponse res = toDTO(a);
                    return res;
                })
                .collect(Collectors.toList());
    }

    // Manager — today only, deduplicated, filtered by team
    public List<AttendanceResponse> getAllForManager(String username) {
        LocalDate today = LocalDate.now();
        Employee manager = employeeRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        return attendanceRepository.findAll().stream()
                .filter(a -> a.getWorkDate().equals(today))
                .filter(a -> a.getEmployee().getManager() != null &&
                        a.getEmployee().getManager().getId().equals(manager.getId()))
                .collect(Collectors.toMap(
                        a -> a.getEmployee().getId(),
                        a -> a,
                        (existing, replacement) -> existing
                ))
                .values().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AttendanceSummaryResponse getSummary(LocalDate from, LocalDate to) {
        long totalEmployees = employeeRepository.count();
        long present  = attendanceRepository.countByStatusAndWorkDateBetween(Attendance.Status.PRESENT,  from, to);
        long absent   = attendanceRepository.countByStatusAndWorkDateBetween(Attendance.Status.ABSENT,   from, to);
        long wfh      = attendanceRepository.countByStatusAndWorkDateBetween(Attendance.Status.WFH,      from, to);
        long onLeave  = attendanceRepository.countByStatusAndWorkDateBetween(Attendance.Status.ON_LEAVE, from, to);
        long field    = attendanceRepository.countByAttendanceTypeAndWorkDateBetween(Attendance.AttendanceType.FIELD, from, to);
        return new AttendanceSummaryResponse(totalEmployees, present, absent, wfh, onLeave, field);
    }

    public List<Map<String, Object>> getAttendanceSummary() {
        return attendanceRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        a -> a.getWorkDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    List<Attendance> weekData = entry.getValue();
                    Map<String, Object> m = new HashMap<>();
                    m.put("week",    entry.getKey().toString());
                    m.put("present", weekData.stream().filter(a -> a.getStatus() == Attendance.Status.PRESENT).count());
                    m.put("absent",  weekData.stream().filter(a -> a.getStatus() == Attendance.Status.ABSENT).count());
                    m.put("leave",   weekData.stream().filter(a -> a.getStatus() == Attendance.Status.ON_LEAVE).count());
                    m.put("wfh",     weekData.stream().filter(a -> a.getStatus() == Attendance.Status.WFH).count());
                    return m;
                })
                .sorted(Comparator.comparing(m -> m.get("week").toString()))
                .collect(Collectors.toList());
    }



    public byte[] exportAttendanceAsCsv() {
        List<Attendance> records = attendanceRepository.findAll();
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Employee ID,Employee Name,Department,Work Date,Check In,Check Out,Duration (min),Type,Status,Regularized,Notes\n");
        records.forEach(a -> {
            String empName = a.getEmployee() != null && a.getEmployee().getUser() != null
                    ? a.getEmployee().getUser().getName() : "N/A";
            String dept    = a.getEmployee() != null && a.getEmployee().getDepartment() != null
                    ? a.getEmployee().getDepartment().getName() : "N/A";
            Long empId     = a.getEmployee() != null ? a.getEmployee().getId() : null;
            csv.append(a.getId()).append(",")
                    .append(empId).append(",")
                    .append(empName).append(",")
                    .append(dept).append(",")
                    .append(a.getWorkDate()).append(",")
                    .append(a.getCheckIn()     != null ? a.getCheckIn()     : "").append(",")
                    .append(a.getCheckOut()    != null ? a.getCheckOut()    : "").append(",")
                    .append(a.getDurationMin() != null ? a.getDurationMin() : "").append(",")
                    .append(a.getAttendanceType()).append(",")
                    .append(a.getStatus()).append(",")
                    .append(a.isRegularized()).append(",")
                    .append(a.getNotes()       != null ? a.getNotes()       : "")
                    .append("\n");
        });
        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}