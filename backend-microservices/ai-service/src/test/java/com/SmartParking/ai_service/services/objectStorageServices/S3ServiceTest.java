package com.SmartParking.ai_service.services.objectStorageServices;

import com.SmartParking.ai_service.exceptions.ObjectStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    private static final String BUCKET_NAME = "bucket";

    @Mock
    private S3Client s3Client;

    @Mock
    private MultipartFile multipartFile;

    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        s3Service = new S3Service(s3Client);
        ReflectionTestUtils.setField(s3Service, "bucketName", BUCKET_NAME);
    }

    @Test
    @DisplayName("Uploads file to S3 when file content is available")
    void uploadFileSuccess() throws Exception {
        when(multipartFile.getOriginalFilename()).thenReturn("file.txt");
        when(multipartFile.getBytes()).thenReturn("content".getBytes());

        s3Service.uploadFile(multipartFile);

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("Throws ObjectStorageException when file bytes cannot be read during upload")
    void uploadFileThrowsWhenBytesUnavailable() throws Exception {
        when(multipartFile.getOriginalFilename()).thenReturn("file.txt");
        when(multipartFile.getBytes()).thenThrow(new IOException("io error"));

        assertThatThrownBy(() -> s3Service.uploadFile(multipartFile))
                .isInstanceOf(ObjectStorageException.class)
                .hasMessageContaining("Failed to read file content for upload");
    }

    @Test
    @DisplayName("Downloads file bytes from S3 successfully")
    void downloadFileSuccess() {
        // Expected bytes
        byte[] expected = "bytes".getBytes();

        // Create a mock ResponseBytes correctly
        ResponseBytes<GetObjectResponse> responseBytes = ResponseBytes.fromByteArray(
                GetObjectResponse.builder().build(), // directly pass the response
                expected
        );

        // Mock the S3 client
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);

        // Call the service method
        byte[] actual = s3Service.downloadFile("key");

        // Assert
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    @DisplayName("Throws ObjectStorageException when S3 reports failure on download")
    void downloadFileThrowsOnS3Error() {
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenThrow(S3Exception.builder().message("failure").build());

        assertThatThrownBy(() -> s3Service.downloadFile("key"))
                .isInstanceOf(ObjectStorageException.class)
                .hasMessageContaining("Failed to download file");
    }
}

