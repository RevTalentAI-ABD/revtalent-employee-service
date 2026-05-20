package com.revtalent.employee_service.controller;

import com.revtalent.employee_service.model.Users;
import com.revtalent.employee_service.model.Employee;
import com.revtalent.employee_service.model.Department;
import com.revtalent.employee_service.repository.UserRepository;
import com.revtalent.employee_service.repository.EmployeeRepository;
import com.revtalent.employee_service.repository.DepartmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hr")
@CrossOrigin("*")
public class HRManagerController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/managers")
    public List<Employee> getManagers() {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getUser() != null && e.getUser().getRole() == Users.Role.MANAGER)
                .collect(Collectors.toList());
    }

    @PutMapping("/assign-manager")
    public Employee assignManager(@RequestBody Map<String, Object> body) {
        Long employeeId = Long.parseLong(body.get("employeeId").toString());
        Long managerId  = Long.parseLong(body.get("managerId").toString());

        Employee emp = employeeRepository.findByUser_Id(employeeId).orElseThrow();
        Employee mgr = employeeRepository.findByUser_Id(managerId).orElseThrow();

        emp.setManager(mgr);
        emp.setDepartment(mgr.getDepartment());
        return employeeRepository.save(emp);
    }

    @PutMapping("/change-department")
    public Employee changeDepartment(@RequestBody Map<String, String> body) {
        Long employeeId = Long.parseLong(body.get("employeeId"));
        String deptName = body.get("department");

        Employee emp = employeeRepository.findByUser_Id(employeeId).orElseThrow();

        Department dept = departmentRepository.findByName(deptName)
                .orElseGet(() -> {
                    Department newDept = new Department();
                    newDept.setName(deptName);
                    return departmentRepository.save(newDept);
                });

        emp.setDepartment(dept);
        employeeRepository.save(emp);

        if (emp.getUser() != null && emp.getUser().getRole() == Users.Role.MANAGER) {
            List<Employee> teamMembers = employeeRepository.findByManager_Id(emp.getId());
            for (Employee member : teamMembers) {
                member.setDepartment(dept);
                employeeRepository.save(member);
            }
        }
        return emp;
    }

    @PutMapping("/change-role")
    public Users changeRole(@RequestBody Map<String, String> body) {
        Long employeeId = Long.parseLong(body.get("employeeId"));
        String role     = body.get("role");

        Users employeeUser = userRepository.findById(employeeId).orElseThrow();
        employeeUser.setRole(Users.Role.valueOf(role));
        return userRepository.save(employeeUser);
    }

    @GetMapping("/managers/{id}/employees")
    public List<Employee> getManagerEmployees(@PathVariable Long id) {
        Employee mgr = employeeRepository.findByUser_Id(id).orElseThrow();
        return employeeRepository.findByManager_Id(mgr.getId());
    }

    @PutMapping("/remove-manager")
    public Employee removeManager(@RequestBody Map<String, Long> body) {
        Long employeeId = body.get("employeeId");
        Employee emp = employeeRepository.findByUser_Id(employeeId).orElseThrow();
        emp.setManager(null);
        return employeeRepository.save(emp);
    }
}
