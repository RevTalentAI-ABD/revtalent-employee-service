package com.revtalent.employee_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "kudos_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"kudos_id", "user_id"})) // prevents duplicates at DB level
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class KudosLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kudos_id", nullable = false)
    private Long kudosId;

    @Column(name = "user_id", nullable = false)  // store username or userId
    private String userId;

    @CreationTimestamp
    private LocalDateTime likedAt;
}