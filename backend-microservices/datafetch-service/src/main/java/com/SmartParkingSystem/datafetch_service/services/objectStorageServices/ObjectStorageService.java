package com.SmartParkingSystem.datafetch_service.services.objectStorageServices;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ObjectStorageService {
    void uploadFile(MultipartFile file) throws IOException;
    byte[] downloadFile(String key);
}
