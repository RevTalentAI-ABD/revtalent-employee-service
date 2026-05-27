package com.revtalent.employee_service.controller;


import com.revtalent.employee_service.dto.*;
import com.revtalent.employee_service.dto.PatchEmployeeRequest;
import com.revtalent.employee_service.dto.UpdateEmployeeRequest;
import com.revtalent.employee_service.dto.EmployeeResponse;
import com.revtalent.employee_service.dto.EmployeeUpdateRequest;
import com.revtalent.employee_service.model.Employee;
import com.revtalent.employee_service.service.EmployeeService;
import com.revtalent.employee_service.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmployeeController {

    private final JwtUtil jwtUtil;


    private final EmployeeService employeeService;


    // ── Employee CRUD (HRModule) ───────────────────────────────────────────────

    @PreAuthorize("hasRole('HR_ADMIN')")
    @GetMapping("/employees")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/employees/directory")
    public ResponseEntity<?> getDirectory() {
        return ResponseEntity.ok(employeeService.getAll());
    }



    @PreAuthorize("hasRole('HR_ADMIN')")
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok("Employee deactivated successfully");
    }

    // ── Employee detail endpoints (HEAD) ──────────────────────────────────────

    @PreAuthorize("hasRole('HR_ADMIN') or @employeeService.getByUsername(authentication.name).id == #id")
    @GetMapping("/employees/{id}")
    public ResponseEntity<com.revtalent.employee_service.dto.EmployeeResponse> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PreAuthorize("hasRole('HR_ADMIN') or @employeeService.getByUsername(authentication.name).id == #id")
    @PutMapping("/employees/{id}")
    public ResponseEntity<com.revtalent.employee_service.dto.EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @PreAuthorize("hasRole('HR_ADMIN') or @employeeService.getByUsername(authentication.name).id == #id")
    @PatchMapping("/employees/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody EmployeeUpdateRequest req) {
        return ResponseEntity.ok(employeeService.update(id, req));
    }

    @PreAuthorize("hasRole('HR_ADMIN') or @employeeService.getByUsername(authentication.name).id == #id")
    @PatchMapping("/employees/{id}/personal-info")
    public ResponseEntity<com.revtalent.employee_service.dto.EmployeeResponse> patchPersonalInfo(
            @PathVariable Long id,
            @RequestBody PatchEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.patchPersonalInfo(id, request));
    }

    @PreAuthorize("hasRole('HR_ADMIN') or @employeeService.getByUsername(authentication.name).id == #id")
    @GetMapping("/employees/{id}/dashboard-stats")
    public ResponseEntity<com.revtalent.employee_service.dto.EmployeeResponse> getDashboardStats(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PreAuthorize("hasRole('HR_ADMIN') or @employeeService.getByUsername(authentication.name).id == #id")
    @GetMapping("/employees/{id}/schedule")
    public ResponseEntity<com.revtalent.employee_service.dto.EmployeeResponse> getSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/employees/announcements")
    public ResponseEntity<List<String>> getAnnouncements() {
        return ResponseEntity.ok(employeeService.getAnnouncements());
    }

    // ── Manager / Team endpoints (HEAD) ───────────────────────────────────────
    // Add this endpoint — reads logged-in user from JWT header
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing token");
        }
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);
        return ResponseEntity.ok(employeeService.getByUsername(username));
    }

    @PreAuthorize("hasRole('HR_ADMIN') or authentication.name == #username")
    @GetMapping("/employees/username/{username}")
    public ResponseEntity<com.revtalent.employee_service.dto.EmployeeResponse> getEmployeeByUsername(@PathVariable String username) {
        return ResponseEntity.ok(employeeService.getByUsername(username));
    }

    @PreAuthorize("hasRole('HR_ADMIN')")
    @PostMapping("/employees")
    public ResponseEntity<?> create(@RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.createEmployee(request));
    }

}