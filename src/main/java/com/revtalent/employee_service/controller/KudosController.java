package com.revtalent.employee_service.controller;

import com.revtalent.employee_service.dto.KudosRequest;
import com.revtalent.employee_service.model.Kudos;
import com.revtalent.employee_service.service.KudosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kudos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class KudosController {

    private final KudosService kudosService;

    @PostMapping
    public ResponseEntity<Kudos> giveKudos(@RequestBody KudosRequest request) {
        return ResponseEntity.ok(kudosService.giveKudos(request));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Kudos>> getKudosForEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(kudosService.getKudosForEmployee(employeeId));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Kudos>> getRecentKudos() {
        return ResponseEntity.ok(kudosService.getRecentKudos());
    }
}
