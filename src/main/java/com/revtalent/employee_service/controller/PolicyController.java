package com.revtalent.employee_service.controller;

import com.revtalent.employee_service.model.Policy;
import com.revtalent.employee_service.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService service;

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('HR_ADMIN')")
    public Policy create(@RequestBody Policy policy) {
        return service.create(policy);
    }

    @GetMapping
    public List<Policy> getAll() {
        return service.getAll();
    }
}
