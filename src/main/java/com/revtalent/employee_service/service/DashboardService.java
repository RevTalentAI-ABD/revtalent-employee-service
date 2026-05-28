package com.revtalent.employee_service.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    public Map<String, Object> getSummary() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalEmployees", 120);
        data.put("pendingLeaves", 6);
        data.put("openJobs", 3);
        return data;
    }
}
