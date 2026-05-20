package com.revtalent.employee_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "JSON")
    private String embeddings; // Store arrays as JSON strings in MySQL, or binary, or ignore if not doing vector search right now
}
