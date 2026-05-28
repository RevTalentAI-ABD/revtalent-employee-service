package com.revtalent.employee_service.service;

import com.revtalent.employee_service.dto.KudosRequest;
import com.revtalent.employee_service.model.Kudos;
import com.revtalent.employee_service.repository.KudosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KudosService {

    private final KudosRepository kudosRepository;
    private final com.revtalent.employee_service.repository.EmployeeRepository employeeRepository;

    public Kudos giveKudos(KudosRequest request, String senderUsername) {
        com.revtalent.employee_service.model.Employee receiver = employeeRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        String receiverName = receiver.getUser().getName();
        String receiverInitials = getInitials(receiverName);
        
        com.revtalent.employee_service.model.Employee sender = null;
        if (senderUsername != null) {
            sender = employeeRepository.findByUser_Username(senderUsername).orElse(null);
        }

        Kudos kudos = Kudos.builder()
                .senderId(sender != null ? sender.getId() : null)
                .receiverId(receiver.getId())
                .message(request.getMessage())
                .category(request.getCategory())
                .senderName(sender != null ? sender.getUser().getName() : "Anonymous")
                .receiverName(receiverName)
                .receiverInitials(receiverInitials)
                .likesCount(0)
                .build();
        return kudosRepository.save(kudos);
    }

    private String getInitials(String name) {
        if (name == null || name.isBlank()) return "??";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    public List<Kudos> getKudosForEmployee(Long receiverId) {
        return kudosRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);
    }

    public List<Kudos> getRecentKudos() {
        return kudosRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    public Kudos likeKudos(Long id) {
        Kudos kudos = kudosRepository.findById(id).orElseThrow(() -> new RuntimeException("Kudos not found"));
        kudos.setLikesCount(kudos.getLikesCount() + 1);
        kudos.setHasLiked(true); // Assuming the current user liked it
        return kudosRepository.save(kudos);
    }
}
