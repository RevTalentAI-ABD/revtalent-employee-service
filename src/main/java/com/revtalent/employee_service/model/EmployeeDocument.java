package com.revtalent.employee_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_document",
        indexes = {
                @Index(name = "idx_document_employee", columnList = "employee_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_document_employee"))
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false,
            columnDefinition = "ENUM('OFFER_LETTER','ID_PROOF','RESUME','CONTRACT','CERTIFICATE','OTHER')")
    private DocType docType;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    public enum DocType {
        OFFER_LETTER,
        ID_PROOF,
        RESUME,
        CONTRACT,
        CERTIFICATE,
        OTHER
    }
}
