package com.dms.documents.service;

import com.dms.documents.model.Document;
import com.dms.documents.repository.DocumentRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final S3StorageService s3StorageService;

    public DocumentService(DocumentRepository documentRepository,
                           S3StorageService s3StorageService) {
        this.documentRepository = documentRepository;
        this.s3StorageService = s3StorageService;
    }

    public List<Document> listAll() {
        List<Document> docs = documentRepository.findAll();
        docs.forEach(this::enrichWithUrl);
        return docs;
    }

    // ── Cacheable: first call hits DB, subsequent calls hit Redis ──
    @Cacheable(value = "documents", key = "#id")
    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id).map(this::enrichWithUrl);
    }

    public Document addDocument(String title) {
        Document doc = new Document();
        doc.setTitle(title);
        return documentRepository.save(doc);
    }

    public Document addDocumentWithFile(String title, MultipartFile file) throws IOException {
        Document doc = new Document();
        doc.setTitle(title);
        if (file != null && !file.isEmpty()) {
            String key = s3StorageService.uploadFile(file);
            doc.setFileKey(key);
        }
        return documentRepository.save(doc);
    }

    // ── CacheEvict: removes the entry from Redis when document is deleted ──
    @CacheEvict(value = "documents", key = "#id")
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }

    private Document enrichWithUrl(Document doc) {
        if (doc.getFileKey() != null && !doc.getFileKey().isBlank()) {
            doc.setFileUrl(s3StorageService.generatePresignedUrl(doc.getFileKey()));
        }
        return doc;
    }
}
