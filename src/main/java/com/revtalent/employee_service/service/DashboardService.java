package com.revtalent.employee_service.service;

import org.springframework.stereotype.Service;

import com.revtalent.employee_service.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EmployeeRepository employeeRepository;

    public Map<String, Object> getSummary() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalEmployees", employeeRepository.count());
        data.put("pendingLeaves", 0);
        data.put("openJobs", 0);
        return data;
    }
}
