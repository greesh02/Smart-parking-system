package com.SmartParkingSystem.datafetch_service.services.objectStorageServices;

import com.SmartParkingSystem.datafetch_service.exceptions.InvalidRequestException;
import com.SmartParkingSystem.datafetch_service.exceptions.ResourceNotFoundException;
import com.SmartParkingSystem.datafetch_service.exceptions.StorageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

@Service
public class S3Service implements ObjectStorageService {

    private static final Logger log = LogManager.getLogger(S3Service.class);

    @Value("${aws.bucket.name}")
    private String bucketName;
    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            throw new InvalidRequestException("A non-empty file with a valid name must be provided");
        }
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(file.getOriginalFilename())
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
            log.info("Uploaded file '{}' to bucket '{}'", file.getOriginalFilename(), bucketName);
        } catch (S3Exception ex) {
            log.error("Failed to upload file '{}' to S3", file.getOriginalFilename(), ex);
            throw new StorageException("Failed to upload file to object storage", ex);
        }
    }

    public byte[] downloadFile(String key) {
        if (key == null || key.isBlank()) {
            throw new InvalidRequestException("Object key must be provided");
        }

        try {
            ResponseBytes<GetObjectResponse> objectAsBytes =
                    s3Client.getObjectAsBytes(GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build());
            log.info("Downloaded file '{}' from bucket '{}'", key, bucketName);
            return objectAsBytes.asByteArray();
        } catch (S3Exception ex) {
            if (ex.statusCode() == 404) {
                log.info("File '{}' not found in bucket '{}'", key, bucketName);
                throw new ResourceNotFoundException("Requested object not found: " + key, ex);
            }
            log.error("Failed to download file '{}' from S3", key, ex);
            throw new StorageException("Failed to download file from object storage", ex);
        }
    }
}