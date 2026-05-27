package com.revtalent.employee_service.service;

import com.revtalent.employee_service.dto.KudosRequest;
import com.revtalent.employee_service.model.Kudos;
import com.revtalent.employee_service.model.KudosLike;
import com.revtalent.employee_service.repository.EmployeeRepository;
import com.revtalent.employee_service.repository.KudosLikeRepository;
import com.revtalent.employee_service.repository.KudosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KudosService {

    private final KudosRepository kudosRepository;
    private final EmployeeRepository employeeRepository;
    private final KudosLikeRepository kudosLikeRepository;

    public Kudos giveKudos(KudosRequest request, String senderUsername) {
        com.revtalent.employee_service.model.Employee receiver = employeeRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        String receiverName = receiver.getUser() != null ? receiver.getUser().getName() : "Unknown";
        String receiverInitials = getInitials(receiverName);

        com.revtalent.employee_service.model.Employee sender = null;
        if (senderUsername != null) {
            sender = employeeRepository.findByUser_Username(senderUsername)
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
        } else {
            throw new RuntimeException("Sender not authenticated");
        }

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Cannot give kudos to yourself");
        }

        Kudos kudos = Kudos.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .message(request.getMessage())
                .category(request.getCategory())
                .senderName(sender.getUser().getName())
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

    public List<Kudos> getRecentKudos(String username) {
        List<Kudos> list = kudosRepository.findTop10ByOrderByCreatedAtDesc();
        if (username != null) {
            list.forEach(k -> k.setHasLiked(
                    kudosLikeRepository.existsByKudosIdAndUserId(k.getId(), username)
            ));
        }
        return list;
    }

    @Transactional
    public Kudos likeKudos(Long id, String username) {
        Kudos kudos = kudosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kudos not found"));

        boolean alreadyLiked = kudosLikeRepository.existsByKudosIdAndUserId(id, username);

        if (alreadyLiked) {
            kudosLikeRepository.deleteByKudosIdAndUserId(id, username);
            kudos.setLikesCount(Math.max(0, kudos.getLikesCount() - 1));
        } else {
            kudosLikeRepository.save(KudosLike.builder()
                    .kudosId(id)
                    .userId(username)
                    .build());
            kudos.setLikesCount(kudos.getLikesCount() + 1);
        }

        Kudos saved = kudosRepository.save(kudos);
        saved.setHasLiked(!alreadyLiked); // re-set after save since @Transient is wiped
        return saved;
    }
}