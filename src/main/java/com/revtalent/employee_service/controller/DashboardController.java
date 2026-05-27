package com.revtalent.employee_service.controller;

import com.revtalent.employee_service.dto.ManagerProfileResponse;
import com.revtalent.employee_service.service.EmployeeService;
import com.revtalent.employee_service.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('MANAGER', 'HR_ADMIN')")

public class DashboardController {

    private final ManagerService managerService;
    private final EmployeeService employeeService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(managerService.getDashboard(auth.getName()));
    }
    @GetMapping("/activity")
    public ResponseEntity<List<Map<String, Object>>> getActivity() {
        return ResponseEntity.ok(managerService.getActivity());
    }

    @GetMapping("/profile")
    public ResponseEntity<ManagerProfileResponse> profile(Principal principal) {
        return ResponseEntity.ok(employeeService.getManagerProfileByUsername(principal.getName()));
    }
    @GetMapping("/team")
    public ResponseEntity<?> getTeam() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(employeeService.getTeamForManager(auth.getName()));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTeam(@RequestParam String query) {
        return ResponseEntity.ok(employeeService.searchTeam(query));
    }

}
