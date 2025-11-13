package com.SmartParking.ai_service.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueueConfig {

    private static final Logger log = LogManager.getLogger(MessageQueueConfig.class);

    @Bean
    public NewTopic messageProcessed() {
        log.debug("Creating Kafka topic bean: messageProcessed");
        return new NewTopic("messageProcessed", 1, (short) 1);
    }
}
