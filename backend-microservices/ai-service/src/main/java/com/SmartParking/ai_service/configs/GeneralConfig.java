package com.SmartParking.ai_service.configs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfig {

	private static final Logger log = LogManager.getLogger(GeneralConfig.class);

    @Bean
    public ObjectMapper objectMapperGenerate(){
        log.debug("Creating ObjectMapper bean");
        return new ObjectMapper();
    }

    @PostConstruct
    public void loadEnv() {
        // to load .env file
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // Add all .env values to system properties
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
		log.info(".env variables loaded into system properties (missing ignored)");
    }
}
