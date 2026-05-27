package com.revtalent.employee_service.repository;

import com.revtalent.employee_service.model.KudosLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KudosLikeRepository extends JpaRepository<KudosLike, Long> {
    boolean existsByKudosIdAndUserId(Long kudosId, String userId);
    void deleteByKudosIdAndUserId(Long kudosId, String userId);
}