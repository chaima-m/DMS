/*
 * Decompiled with CFR 0.152.
 */
package com.dms.documents.event;

import java.time.LocalDateTime;

public class DocumentUploadedEvent {
    private Long id;
    private String uid;
    private String title;
    private String description;
    private String fileKey;
    private LocalDateTime uploadedAt;

    public DocumentUploadedEvent() {
    }

    public DocumentUploadedEvent(Long id, String uid, String title, String description, String fileKey, LocalDateTime uploadedAt) {
        this.id = id;
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.fileKey = fileKey;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileKey() {
        return this.fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public LocalDateTime getUploadedAt() {
        return this.uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
