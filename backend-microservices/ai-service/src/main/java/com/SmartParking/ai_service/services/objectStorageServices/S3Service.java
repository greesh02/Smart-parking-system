package com.SmartParking.ai_service.services.objectStorageServices;

import com.SmartParking.ai_service.exceptions.ObjectStorageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3Service implements ObjectStorageService {

    private static final Logger log = LogManager.getLogger(S3Service.class);

    @Value("${aws.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void uploadFile(MultipartFile file) {
        try {
            log.info("Uploading file to S3: {}", file.getOriginalFilename());
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(file.getOriginalFilename())
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
        } catch (java.io.IOException ex) {
            String message = "Failed to read file content for upload";
            log.error("{}: {}", message, ex.getMessage(), ex);
            throw new ObjectStorageException(message, ex);
        } catch (S3Exception | SdkClientException ex) {
            String message = "Failed to upload file to S3 bucket " + bucketName;
            log.error("{}: {}", message, ex.getMessage(), ex);
            throw new ObjectStorageException(message, ex);
        }
    }

    @Override
    public byte[] downloadFile(String key) {
        try {
            log.info("Downloading file from S3: {}", key);
            ResponseBytes<GetObjectResponse> objectAsBytes =
                    s3Client.getObjectAsBytes(GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build());
            return objectAsBytes.asByteArray();
        } catch (S3Exception | SdkClientException ex) {
            String message = "Failed to download file %s from S3 bucket %s".formatted(key, bucketName);
            log.error("{}: {}", message, ex.getMessage(), ex);
            throw new ObjectStorageException(message, ex);
        }
    }
}

