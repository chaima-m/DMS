/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.http.ResponseEntity
 *  org.springframework.messaging.Message
 *  org.springframework.messaging.support.MessageBuilder
 *  org.springframework.stereotype.Service
 *  org.springframework.web.client.RestClientException
 *  org.springframework.web.client.RestTemplate
 */
package com.dms.esb.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class OrchestrationService {
    private static final Logger log = LoggerFactory.getLogger(OrchestrationService.class);
    private final RestTemplate restTemplate;
    @Value(value="${services.documents.url}")
    private String documentsUrl;
    @Value(value="${services.comments.url}")
    private String commentsUrl;

    public OrchestrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getDocumentWithComments(Long id) {
        Message inputMessage = MessageBuilder.withPayload((Object)id).setHeader("documentId", (Object)id).build();
        Map<String, Object> document = this.fetchDocument((Message<Long>)inputMessage);
        List<?> comments = this.fetchComments((Message<Long>)inputMessage);
        Message aggregated = MessageBuilder.withPayload(Map.of("document", document, "comments", comments)).copyHeadersIfAbsent((Map)inputMessage.getHeaders()).build();
        return (Map)aggregated.getPayload();
    }

    private Map<String, Object> fetchDocument(Message<Long> msg) {
        String url = this.documentsUrl + "/documents/get/" + String.valueOf(msg.getPayload());
        ResponseEntity response = this.restTemplate.getForEntity(url, Map.class, new Object[0]);
        return (Map)response.getBody();
    }

    private List<?> fetchComments(Message<Long> msg) {
        String url = this.commentsUrl + "/comments/list/" + String.valueOf(msg.getPayload());
        try {
            ResponseEntity response = this.restTemplate.getForEntity(url, List.class, new Object[0]);
            return response.getBody() != null ? (List)response.getBody() : Collections.emptyList();
        }
        catch (RestClientException e) {
            log.warn("Comments service unavailable for docId={}: {}", msg.getPayload(), (Object)e.getMessage());
            return Collections.emptyList();
        }
    }
}
