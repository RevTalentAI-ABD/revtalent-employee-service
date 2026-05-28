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

public class KudosController {

    private final KudosService kudosService;
    private final com.revtalent.employee_service.util.JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<Kudos> giveKudos(@RequestBody KudosRequest request, jakarta.servlet.http.HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
        }
        return ResponseEntity.ok(kudosService.giveKudos(request, username));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Kudos>> getKudosForEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(kudosService.getKudosForEmployee(employeeId));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Kudos>> getRecentKudos() {
        return ResponseEntity.ok(kudosService.getRecentKudos());
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<Kudos> likeKudos(@PathVariable Long id) {
        return ResponseEntity.ok(kudosService.likeKudos(id));
    }
}
