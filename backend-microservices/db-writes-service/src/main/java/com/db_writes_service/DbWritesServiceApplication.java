package com.db_writes_service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DbWritesServiceApplication {

	private static final Logger log = LogManager.getLogger(DbWritesServiceApplication.class);

	public static void main(String[] args) {
		log.info("Starting DB Writes Service application");
		SpringApplication.run(DbWritesServiceApplication.class, args);
		log.info("DB Writes Service application started");
	}

}
