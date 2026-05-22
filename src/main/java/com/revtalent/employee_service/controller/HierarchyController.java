package com.revtalent.employee_service.controller;
import com.revtalent.employee_service.service.HierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hierarchy")
@RequiredArgsConstructor

public class HierarchyController {

    private final HierarchyService hierarchyService;

    /**
     * GET /api/hierarchy/managers
     * Returns all managers with their assigned employees.
     * Response shape: [{ id, name, role, avatar, color, employees: [{ id, name, avatar, color }] }]
     */
    @GetMapping("/managers")
    public ResponseEntity<List<Map<String, Object>>> getManagers() {
        return ResponseEntity.ok(hierarchyService.getManagersWithTeams());
    }

    /**
     * GET /api/hierarchy/unassigned
     * Returns all employees who have no manager assigned.
     * Response shape: [{ id, name, avatar, color }]
     */
    @GetMapping("/unassigned")
    public ResponseEntity<List<Map<String, Object>>> getUnassigned() {
        return ResponseEntity.ok(hierarchyService.getUnassignedEmployees());
    }

    /**
     * POST /api/hierarchy/assign
     * Assigns a list of employees to a manager.
     * Body: { "managerId": 1, "employeeIds": [201, 202] }
     */
    @PostMapping("/assign")
    public ResponseEntity<Map<String, Object>> assignEmployees(
            @RequestBody Map<String, Object> body) {

        Long managerId = Long.valueOf(body.get("managerId").toString());

        @SuppressWarnings("unchecked")
        List<Integer> rawIds = (List<Integer>) body.get("employeeIds");
        List<Long> employeeIds = rawIds.stream().map(Long::valueOf).toList();

        hierarchyService.assignEmployeesToManager(managerId, employeeIds);
        return ResponseEntity.ok(Map.of("success", true));
    }

    /**
     * POST /api/hierarchy/unassign
     * Unassigns a list of employees from their current manager.
     * Body: { "employeeIds": [201, 202] }
     */
    @PostMapping("/unassign")
    public ResponseEntity<Map<String, Object>> unassignEmployees(
            @RequestBody Map<String, Object> body) {

        @SuppressWarnings("unchecked")
        List<Integer> rawIds = (List<Integer>) body.get("employeeIds");
        List<Long> employeeIds = rawIds.stream().map(Long::valueOf).toList();

        hierarchyService.unassignEmployeesFromManager(employeeIds);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
