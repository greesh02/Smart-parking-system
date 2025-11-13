package com.SmartParking.ai_service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiServiceApplication {

    private static final Logger log = LogManager.getLogger(AiServiceApplication.class);

    public static void main(String[] args) {
        log.info("Starting AI Service application");
        SpringApplication.run(AiServiceApplication.class, args);
        log.info("AI Service application started successfully");
    }
}
