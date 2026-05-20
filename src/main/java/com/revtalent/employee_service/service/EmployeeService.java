package com.revtalent.employee_service.service;

import com.revtalent.employee_service.dto.CreateEmployeeRequest;

import com.revtalent.employee_service.dto.PatchEmployeeRequest;
import com.revtalent.employee_service.dto.UpdateEmployeeRequest;
import com.revtalent.employee_service.dto.EmployeeUpdateRequest;
import com.revtalent.employee_service.dto.ManagerProfileResponse;
import com.revtalent.employee_service.exception.ResourceNotFoundException;
import com.revtalent.employee_service.model.Department;
import com.revtalent.employee_service.model.Employee;
import com.revtalent.employee_service.model.Users;
import com.revtalent.employee_service.dto.EmployeeResponse;
import com.revtalent.employee_service.repository.DepartmentRepository;
import com.revtalent.employee_service.repository.EmployeeRepository;
import com.revtalent.employee_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // ── Mapper ────────────────────────────────────────────────────────────────

    private EmployeeResponse toResponse(Employee emp) {
        return EmployeeResponse.builder()
                .id(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .name(emp.getUser() != null ? emp.getUser().getName() : "N/A")
                .username(emp.getUser() != null ? emp.getUser().getUsername() : "N/A")
                .email(emp.getUser() != null ? emp.getUser().getEmail() : "N/A")
                .role(emp.getUser() != null && emp.getUser().getRole() != null
                        ? emp.getUser().getRole().name() : null)
                .designation(emp.getDesignation())
                .departmentId(emp.getDepartment() != null ? emp.getDepartment().getId() : null)
                .departmentName(emp.getDepartment() != null ? emp.getDepartment().getName() : "N/A")
                .managerId(emp.getManager() != null ? emp.getManager().getId() : null)
                .managerName(emp.getManager() != null && emp.getManager().getUser() != null  // ← add this
                        ? emp.getManager().getUser().getName() : "Not Assigned")
                .status(emp.getStatus() != null ? emp.getStatus().name() : null)
                .phone(emp.getPhone())
                .address(emp.getAddress())
                .gender(emp.getGender())
                .profilePictureUrl(emp.getProfilePictureUrl())
                .joiningDate(emp.getJoiningDate())
                .build();
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // From HRModule — returns only ACTIVE employees
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {
        return employeeRepository.findByStatus(Employee.Status.ACTIVE)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        return toResponse(employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id)));
    }

    public ManagerProfileResponse getManagerProfileByUsername(String username) {
        Employee manager = employeeRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Manager not found: " + username));
        return getManagerProfile(manager.getId());
    }
    // Alias used by HRModule routes
    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return getEmployeeById(id);
    }

    // ── Create ────────────────────────────────────────────────────────────────

    public List<EmployeeResponse> getTeamForManager(String username) {
        Employee manager = employeeRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        return employeeRepository.findByManager_Id(manager.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }

        employeeRepository.findByUser_Username(request.getUsername()).ifPresent(e -> {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        });
        employeeRepository.findByUser_Email(request.getEmail()).ifPresent(e -> {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        });

        Users users = new Users();
        users.setName(request.getName());
        users.setUsername(request.getUsername());
        users.setEmail(request.getEmail());
        users.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getRole() != null && !request.getRole().isBlank()) {
            users.setRole(Users.Role.valueOf(request.getRole().toUpperCase()));
        } else {
            users.setRole(Users.Role.EMPLOYEE);
        }

        Employee emp = new Employee();
        emp.setUser(users);
        emp.setEmployeeCode(request.getEmployeeCode());
        emp.setDesignation(request.getDesignation());
        emp.setJoiningDate(request.getJoiningDate());
        emp.setPhone(request.getPhone());
        emp.setAddress(request.getAddress());

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", request.getDepartmentId()));
            emp.setDepartment(dept);
        }

        return toResponse(employeeRepository.save(emp));
    }

    public Employee create(Employee emp) {

        emp.setEmployeeCode("EMP" + System.currentTimeMillis());

        Users users = new Users();

        String email = emp.getEmail();

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (employeeRepository.findByUser_Email(email).isPresent()) {
            throw new RuntimeException("User already exists with this email");
        }

        users.setUsername(email);
        users.setEmail(email);

        // ✅ 🔥 CRITICAL FIX
        users.setPasswordHash(passwordEncoder.encode("default123"));

        users.setRole(Users.Role.EMPLOYEE);
        users.setActive(true);

        // ✅ attach user
        emp.setUser(users);

        // ✅ department mapping
        if (emp.getDepartment() != null && emp.getDepartment().getName() != null) {
            Department dept = departmentRepository
                    .findByName(emp.getDepartment().getName())
                    .orElseThrow(() -> new RuntimeException("Department not found"));

            emp.setDepartment(dept);
        }

        if (emp.getDesignation() == null || emp.getDesignation().isBlank()) {
            emp.setDesignation("Employee");
        }

        if (emp.getJoiningDate() == null) {
            emp.setJoiningDate(LocalDate.now());
        }

        return employeeRepository.save(emp);
    }
    @Transactional
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {
        Employee emp = findOrThrow(id);

        if (request.getDesignation() != null && !request.getDesignation().isBlank()) {
            emp.setDesignation(request.getDesignation());
        }
        if (request.getPhone() != null) {
            emp.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            emp.setAddress(request.getAddress());
        }
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", request.getDepartmentId()));
            emp.setDepartment(dept);
        }
        Employee.Status parsedStatus = request.parsedStatus();
        if (parsedStatus != null) {
            emp.setStatus(parsedStatus);
        }

        return toResponse(employeeRepository.save(emp));
    }

    // From HRModule — accepts EmployeeUpdateRequest DTO
    @Transactional
    public EmployeeResponse update(Long id, EmployeeUpdateRequest req) {
        Employee emp = findOrThrow(id);

        if (req.getDesignation() != null) emp.setDesignation(req.getDesignation());
        if (req.getPhone() != null)       emp.setPhone(req.getPhone());
        if (req.getAddress() != null)     emp.setAddress(req.getAddress());
        if (req.getDateOfBirth() != null) emp.setDateOfBirth(req.getDateOfBirth());
        if (req.getGender() != null)      emp.setGender(req.getGender());

        return toResponse(employeeRepository.save(emp));
    }

    @Transactional
    public EmployeeResponse patchPersonalInfo(Long id, PatchEmployeeRequest request) {
        Employee emp = findOrThrow(id);

        if (request.getPhone() != null) emp.setPhone(request.getPhone());
        if (request.getAddress() != null) emp.setAddress(request.getAddress());
        if (request.getDesignation() != null) emp.setDesignation(request.getDesignation());
        if (request.getProfilePictureUrl() != null) emp.setProfilePictureUrl(request.getProfilePictureUrl());

        if (emp.getUser() != null) {
            if (request.getName() != null) emp.getUser().setName(request.getName());
            if (request.getEmail() != null) emp.getUser().setEmail(request.getEmail());
        }

        return toResponse(employeeRepository.save(emp));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    public void deleteEmployee(Long id) {
        findOrThrow(id);
        employeeRepository.deleteById(id);
    }

    // From HRModule — soft delete (sets status to INACTIVE)
    @Transactional
    public boolean delete(Long id) {
        Employee emp = findOrThrow(id);
        emp.setStatus(Employee.Status.INACTIVE);
        employeeRepository.save(emp);
        return true;
    }

    // ── Search & Team ─────────────────────────────────────────────────────────

    public List<EmployeeResponse> getTeam() {
        return employeeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<EmployeeResponse> searchTeam(String query) {
        String q = query.toLowerCase();
        return employeeRepository.findAll().stream()
                .filter(emp -> emp.getUser() != null &&
                        emp.getUser().getUsername() != null &&
                        emp.getUser().getUsername().toLowerCase().contains(q))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Manager ───────────────────────────────────────────────────────────────

    public ManagerProfileResponse getManagerProfile(Long managerId) {
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));

        int teamSize = (int) employeeRepository.countByManagerId(managerId);

        return ManagerProfileResponse.builder()
                .id(manager.getId())
                .name(manager.getUser() != null ? manager.getUser().getName() : "N/A")
                .username(manager.getUser() != null ? manager.getUser().getUsername() : "N/A")
                .email(manager.getUser() != null ? manager.getUser().getEmail() : "N/A")
                .designation(manager.getDesignation())
                .department(manager.getDepartment() != null ? manager.getDepartment().getName() : "N/A")
                .employeeCode(manager.getEmployeeCode())
                .phone(manager.getPhone())
                .gender(manager.getGender())
                .joiningDate(manager.getJoiningDate())
                .dateOfBirth(manager.getDateOfBirth())
                .address(manager.getAddress())
                .profilePictureUrl(manager.getProfilePictureUrl())
                .status(manager.getStatus().name())
                .teamSize(teamSize)
                .build();
    }

    // ── Misc ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<String> getAnnouncements() {
        return List.of("Welcome to RevTalent!", "Q2 performance reviews begin next week.");
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Employee findOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }
    @Transactional(readOnly = true)
    public EmployeeResponse getByUsername(String username) {
        Employee emp = employeeRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", 0L));
        return toResponse(emp);
    }
}