package com.revtalent.employee_service.repository;

import com.revtalent.employee_service.model.Kudos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KudosRepository extends JpaRepository<Kudos, Long> {
    List<Kudos> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    List<Kudos> findTop10ByOrderByCreatedAtDesc();
}
