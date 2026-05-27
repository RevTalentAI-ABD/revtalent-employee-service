package com.revtalent.employee_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;

@Entity
@Table(name = "kudos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kudos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "badge_type", nullable = false)
    private String category;

    private String senderName;
    private String receiverName;
    private String receiverInitials;

    private int likesCount = 0;
    
    @Transient
    private boolean hasLiked = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
