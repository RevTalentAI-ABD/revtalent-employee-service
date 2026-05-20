package com.revtalent.employee_service.controller;

import com.revtalent.employee_service.dto.AttendanceDTO;
import com.revtalent.employee_service.dto.AttendanceResponseDTO;
import com.revtalent.employee_service.dto.AttendanceResponse;
import com.revtalent.employee_service.dto.AttendanceSummaryResponse;
import com.revtalent.employee_service.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // ── Employee-scoped endpoints ──────────────────────────────────────────────

    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<AttendanceResponseDTO>> getByEmployee(@PathVariable Long empId) {
        return ResponseEntity.ok(attendanceService.getByEmployee(empId));
    }

    @GetMapping("/employee/{empId}/range")
    public ResponseEntity<List<AttendanceResponseDTO>> getByRange(
            @PathVariable Long empId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(attendanceService.getByEmployeeAndDateRange(empId, from, to));
    }

    @PostMapping("/employee/{empId}/checkin")
    public ResponseEntity<AttendanceResponseDTO> checkIn(
            @PathVariable Long empId,
            @RequestBody AttendanceDTO dto) {
        return ResponseEntity.ok(attendanceService.checkIn(empId, dto));
    }

    @PutMapping("/employee/{empId}/checkout")
    public ResponseEntity<AttendanceResponseDTO> checkOut(@PathVariable Long empId) {
        return ResponseEntity.ok(attendanceService.checkOut(empId));
    }

    @PostMapping("/employee/{empId}")
    public ResponseEntity<AttendanceResponseDTO> save(
            @PathVariable Long empId,
            @RequestBody AttendanceDTO dto) {
        return ResponseEntity.ok(attendanceService.save(empId, dto));
    }

    @PutMapping("/{attendanceId}/regularize")
    public ResponseEntity<AttendanceResponseDTO> regularize(
            @PathVariable Long attendanceId,
            @RequestBody AttendanceDTO dto) {
        return ResponseEntity.ok(attendanceService.regularize(attendanceId, dto));
    }

    @GetMapping("/employee/{empId}/present-count")
    public ResponseEntity<Integer> getPresentCount(
            @PathVariable Long empId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(attendanceService.getPresentCount(empId, from, to));
    }

    // ── HR / Manager endpoints ────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isManager = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));

        if (isManager) {
            return ResponseEntity.ok(attendanceService.getAllForManager(auth.getName()));
        }
        return ResponseEntity.ok(attendanceService.getAll());
    }

    @GetMapping("/summary")
    public ResponseEntity<List<Map<String, Object>>> getSummary() {
        return ResponseEntity.ok(attendanceService.getAttendanceSummary());
    }

    @GetMapping("/hr/summary")
    public ResponseEntity<AttendanceSummaryResponse> getHrSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(attendanceService.getSummary(from, to));
    }


}