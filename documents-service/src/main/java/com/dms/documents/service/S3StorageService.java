/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Service
 *  org.springframework.web.multipart.MultipartFile
 *  software.amazon.awssdk.core.sync.RequestBody
 *  software.amazon.awssdk.services.s3.S3Client
 *  software.amazon.awssdk.services.s3.model.CreateBucketRequest
 *  software.amazon.awssdk.services.s3.model.HeadBucketRequest
 *  software.amazon.awssdk.services.s3.model.NoSuchBucketException
 *  software.amazon.awssdk.services.s3.model.PutObjectRequest
 *  software.amazon.awssdk.services.s3.presigner.S3Presigner
 *  software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
 */
package com.dms.documents.service;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class S3StorageService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    @Value(value="${app.s3.bucket}")
    private String bucket;
    @Value(value="${app.s3.presign-duration-seconds}")
    private long presignDurationSeconds;
    @Value(value="${app.s3.public-endpoint:${app.s3.endpoint}}")
    private String publicEndpoint;

    public S3StorageService(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        this.ensureBucketExists();
        String key = "documents/" + String.valueOf(UUID.randomUUID()) + "/" + file.getOriginalFilename();
        this.s3Client.putObject((PutObjectRequest)PutObjectRequest.builder().bucket(this.bucket).key(key).contentType(file.getContentType()).build(), RequestBody.fromBytes((byte[])file.getBytes()));
        return key;
    }

    public String generatePresignedUrl(String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder().signatureDuration(Duration.ofSeconds(this.presignDurationSeconds)).getObjectRequest(r -> r.bucket(this.bucket).key(key)).build();
        String internalUrl = this.s3Presigner.presignGetObject(presignRequest).url().toString();
        if (this.publicEndpoint != null && !this.publicEndpoint.equals(this.getS3Endpoint())) {
            String s3Host = this.extractHost(this.getS3Endpoint());
            String publicHost = this.extractHost(this.publicEndpoint);
            internalUrl = internalUrl.replace(s3Host, publicHost);
        }
        return internalUrl;
    }

    private String getS3Endpoint() {
        return "http://minio:9000";
    }

    private String extractHost(String url) {
        try {
            URI uri = URI.create(url);
            return uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort();
        }
        catch (Exception e) {
            return url;
        }
    }

    private void ensureBucketExists() {
        try {
            this.s3Client.headBucket((HeadBucketRequest)HeadBucketRequest.builder().bucket(this.bucket).build());
        }
        catch (NoSuchBucketException e) {
            this.s3Client.createBucket((CreateBucketRequest)CreateBucketRequest.builder().bucket(this.bucket).build());
        }
    }
}
