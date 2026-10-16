/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.data.jpa.repository.JpaRepository
 *  org.springframework.data.jpa.repository.Modifying
 *  org.springframework.data.jpa.repository.Query
 *  org.springframework.data.repository.query.Param
 *  org.springframework.stereotype.Repository
 */
package com.dms.documents.repository;

import com.dms.documents.model.Document;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository
extends JpaRepository<Document, Long> {
    public List<Document> findByDepartmentIn(List<String> var1);

    public List<Document> findByDepartment(String var1);

    public List<Document> findByOwnerEmail(String var1);

    @Query(value="SELECT d FROM Document d WHERE d.expiresAt IS NOT NULL AND d.expiresAt < :today AND d.status <> 'ARCHIVED'")
    public List<Document> findExpired(@Param(value="today") LocalDate var1);

    @Query(value="SELECT d FROM Document d WHERE d.expiresAt IS NOT NULL AND d.expiresAt >= :today AND d.expiresAt <= :cutoff AND d.status = 'ACTIVE'")
    public List<Document> findExpiringSoon(@Param(value="today") LocalDate var1, @Param(value="cutoff") LocalDate var2);

    @Modifying
    @Query(value="UPDATE Document d SET d.status = :status WHERE d.id IN :ids")
    public void bulkUpdateStatus(@Param(value="ids") List<Long> var1, @Param(value="status") String var2);

    @Query(value="SELECT d FROM Document d WHERE d.department IN :depts AND d.status <> 'ARCHIVED'")
    public List<Document> findActiveByDepartmentIn(@Param(value="depts") List<String> var1);

    @Query(value="SELECT d FROM Document d WHERE d.status <> 'ARCHIVED'")
    public List<Document> findAllActive();
}
