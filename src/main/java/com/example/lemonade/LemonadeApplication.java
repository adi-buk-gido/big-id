package com.example.lemonade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LemonadeApplication {

	    private static final Logger logger = LoggerFactory.getLogger(LemonadeApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Spring Boot application...");
		SpringApplication.run(LemonadeApplication.class, args);
		logger.info("Application started successfully.");
	}

}
