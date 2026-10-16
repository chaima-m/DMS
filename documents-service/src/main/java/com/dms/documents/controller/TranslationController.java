/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.http.ResponseEntity
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.dms.documents.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/translations"})
public class TranslationController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping(value={"/list"})
    public ResponseEntity<List<Map<String, Object>>> listTranslations() {
        try {
            List<Map<String, Object>> results = this.jdbcTemplate.queryForList("SELECT id, document_uid, source_document_id, original_title, translated_title, original_description, translated_description, summary, target_language, model, created_at FROM translations ORDER BY created_at DESC LIMIT 100");
            return ResponseEntity.ok(results);
        }
        catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping(value={"/document/{uid}"})
    public ResponseEntity<List<Map<String, Object>>> getByDocument(@PathVariable String uid) {
        try {
            List<Map<String, Object>> results = this.jdbcTemplate.queryForList("SELECT id, document_uid, source_document_id, original_title, translated_title, original_description, translated_description, summary, target_language, model, created_at FROM translations WHERE document_uid = ? ORDER BY created_at DESC", new Object[]{uid});
            return ResponseEntity.ok(results);
        }
        catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
}
