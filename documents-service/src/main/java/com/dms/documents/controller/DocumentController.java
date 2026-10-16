package com.dms.documents.controller;

import com.dms.documents.model.Document;
import com.dms.documents.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // ─── GET /documents/list ─────────────────────────────────────────────────

    @GetMapping("/list")
    public ResponseEntity<List<Document>> listDocuments() {
        return ResponseEntity.ok(documentService.listAll());
    }

    // ─── GET /documents/get/{id} ─────────────────────────────────────────────

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getDocument(@PathVariable Long id) {
        return documentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── POST /documents/add  (title only — no file) ─────────────────────────

    @PostMapping("/add")
    public ResponseEntity<Document> addDocument(@RequestBody Map<String, String> body) {
        String title = body.get("title");
        if (title == null || title.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(documentService.addDocument(title));
    }

    // ─── POST /documents/add/with-file  (title + file upload) ────────────────

    @PostMapping("/add/with-file")
    public ResponseEntity<?> addDocumentWithFile(
            @RequestParam("title") String title,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            Document saved = documentService.addDocumentWithFile(title, file);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "File upload failed: " + e.getMessage()));
        }
    }

    // DELETE /documents/delete/{id}
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(Map.of("message", "Document deleted and cache evicted"));
    }
}
