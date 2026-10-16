package com.dms.documents.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Universal Unique ID for external reference (optional exercise P1)
    @Column(unique = true, nullable = false, updatable = false)
    private String uid;

    @Column(nullable = false)
    private String title;

    // S3 key for the attached file
    private String fileKey;

    // Presigned URL (transient — not stored in DB)
    @Transient
    private String fileUrl;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.uid = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
}
