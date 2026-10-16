/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.RestController
 */
package com.dms.esb.controller;

import com.dms.esb.service.OrchestrationService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrchestrationController {
    private final OrchestrationService orchestrationService;

    public OrchestrationController(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @GetMapping(value={"/document/{id}"})
    public ResponseEntity<Map<String, Object>> getDocument(@PathVariable Long id) {
        try {
            Map<String, Object> result = this.orchestrationService.getDocumentWithComments(id);
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Document not found or service unavailable: " + e.getMessage()));
        }
    }
}
