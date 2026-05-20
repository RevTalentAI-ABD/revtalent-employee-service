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

    public Kudos giveKudos(KudosRequest request) {
        Kudos kudos = Kudos.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .message(request.getMessage())
                .badgeType(request.getBadgeType())
                .build();
        return kudosRepository.save(kudos);
    }

    public List<Kudos> getKudosForEmployee(Long receiverId) {
        return kudosRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);
    }

    public List<Kudos> getRecentKudos() {
        return kudosRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
