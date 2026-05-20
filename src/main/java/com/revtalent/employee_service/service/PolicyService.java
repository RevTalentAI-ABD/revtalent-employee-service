package com.revtalent.employee_service.service;

import com.revtalent.employee_service.model.Policy;
import com.revtalent.employee_service.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository repository;

    public Policy create(Policy policy) {
        return repository.save(policy);
    }

    public List<Policy> getAll() {
        return repository.findAll();
    }
}
