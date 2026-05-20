package com.revtalent.employee_service.service;

import com.revtalent.employee_service.model.Employee;
import com.revtalent.employee_service.model.Users;
import com.revtalent.employee_service.repository.EmployeeRepository;
import com.revtalent.employee_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HierarchyService {

    private final UserRepository usersRepository;
    private final EmployeeRepository employeeRepository;

    private static final String[] COLORS = {
            "#7C3AED", "#06B6D4", "#10B981", "#EC4899",
            "#F59E0B", "#8B5CF6", "#14B8A6", "#EF4444",
            "#3B82F6", "#F97316"
    };

    public List<Map<String, Object>> getManagersWithTeams() {
        List<Employee> managers = employeeRepository.findAll().stream()
                .filter(e -> e.getUser() != null && 
                        (e.getUser().getRole() == Users.Role.MANAGER || e.getUser().getRole() == Users.Role.HR_ADMIN))
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();

        for (Employee manager : managers) {
            List<Employee> team = employeeRepository.findByManager_Id(manager.getId());

            List<Map<String, Object>> employeeDtos = team.stream()
                    .map(this::toDto)
                    .toList();

            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id",         manager.getUser().getId());
            dto.put("name",       manager.getUser().getName());
            dto.put("role",       friendlyRole(manager.getUser().getRole()));
            dto.put("department", manager.getDepartment() != null ? manager.getDepartment().getName() : "N/A");
            dto.put("avatar",     initials(manager.getUser().getName()));
            dto.put("color",      colorFor(manager.getUser().getId()));
            dto.put("employees",  employeeDtos);
            result.add(dto);
        }

        return result;
    }

    public List<Map<String, Object>> getUnassignedEmployees() {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getManager() == null && e.getUser() != null && e.getUser().getRole() == Users.Role.EMPLOYEE)
                .map(this::toDto)
                .toList();
    }

    @org.springframework.transaction.annotation.Transactional
    public void assignEmployeesToManager(Long managerUserId, List<Long> employeeUserIds) {
        Employee managerEntity = employeeRepository.findByUser_Id(managerUserId)
                .orElseThrow(() -> new RuntimeException("Manager not found: " + managerUserId));

        for (Long empUserId : employeeUserIds) {
            Employee employeeEntity = employeeRepository.findByUser_Id(empUserId).orElse(null);
            if (employeeEntity != null) {
                employeeEntity.setManager(managerEntity);
                employeeEntity.setDepartment(managerEntity.getDepartment());
                employeeRepository.save(employeeEntity);
            }
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public void unassignEmployeesFromManager(List<Long> employeeUserIds) {
        for (Long empUserId : employeeUserIds) {
            Employee employeeEntity = employeeRepository.findByUser_Id(empUserId).orElse(null);
            if (employeeEntity != null) {
                employeeEntity.setManager(null);
                employeeRepository.save(employeeEntity);
            }
        }
    }

    private Map<String, Object> toDto(Employee e) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (e.getUser() != null) {
            dto.put("id",         e.getUser().getId());
            dto.put("name",       e.getUser().getName());
            dto.put("role",       friendlyRole(e.getUser().getRole()));
            dto.put("email",      e.getUser().getEmail());
            dto.put("avatar",     initials(e.getUser().getName()));
            dto.put("color",      colorFor(e.getUser().getId()));
        }
        dto.put("department", e.getDepartment() != null ? e.getDepartment().getName() : "N/A");
        return dto;
    }

    private String friendlyRole(Users.Role role) {
        if (role == null) return "Employee";
        return switch (role) {
            case MANAGER   -> "Manager";
            case HR_ADMIN  -> "HR Admin";
            case EMPLOYEE  -> "Employee";
            case CANDIDATE -> "Candidate";
        };
    }

    private String initials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) { if (!p.isEmpty()) sb.append(p.charAt(0)); }
        return sb.toString().toUpperCase().substring(0, Math.min(2, sb.length()));
    }

    private String colorFor(Long id) {
        return COLORS[(int)(id % COLORS.length)];
    }
}
