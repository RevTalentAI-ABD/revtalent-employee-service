package com.revtalent.employee_service.controller;

import com.revtalent.employee_service.dto.KudosRequest;
import com.revtalent.employee_service.model.Kudos;
import com.revtalent.employee_service.service.KudosService;
import com.revtalent.employee_service.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kudos")
@RequiredArgsConstructor
@org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
public class KudosController {

    private final KudosService kudosService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<Kudos> giveKudos(@RequestBody KudosRequest request,
                                           HttpServletRequest httpRequest) {
        return ResponseEntity.ok(kudosService.giveKudos(request, extractUsername(httpRequest)));
    }

    @GetMapping("/employee/{employeeId}")
    @org.springframework.security.access.prepost.PreAuthorize(
            "hasRole('HR_ADMIN') or hasRole('MANAGER') or @employeeService.getByUsername(authentication.name).id == #employeeId")
    public ResponseEntity<List<Kudos>> getKudosForEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(kudosService.getKudosForEmployee(employeeId));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Kudos>> getRecentKudos(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(kudosService.getRecentKudos(extractUsername(httpRequest)));
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<?> likeKudos(@PathVariable Long id,
                                       HttpServletRequest httpRequest) {
        String username = extractUsername(httpRequest);
        if (username == null) return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(kudosService.likeKudos(id, username));
    }

    private String extractUsername(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
        }
        return null;
    }
}