package com.SmartParking.ai_service.configs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmConfig {

    private static final Logger log = LogManager.getLogger(LlmConfig.class);

    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel chatModel) {
        log.debug("Creating ChatClient bean backed by OpenAI");
        return ChatClient.create(chatModel);
    }
}