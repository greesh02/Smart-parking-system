package com.SmartParking.ai_service.services.objectStorageServices;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ObjectStorageService {
    void uploadFile(MultipartFile file);
    byte[] downloadFile(String key);
}
