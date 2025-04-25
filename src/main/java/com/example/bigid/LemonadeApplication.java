package com.example.bigid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.bigid.service.StringFinderService;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class LemonadeApplication {

	    private static final Logger logger = LoggerFactory.getLogger(LemonadeApplication.class);

	public static void main(String[] args) throws Exception {
		logger.info("Starting Spring Boot application...");
		SpringApplication.run(LemonadeApplication.class, args);
		logger.info("Application started successfully.");

		ApplicationContext context = SpringApplication.run(LemonadeApplication.class, args);

        StringFinderService stringFinderService = context.getBean(StringFinderService.class);

		List<String> stringsToFind = new ArrayList<>(Arrays.asList(
            "James", "John", "Robert", "Michael", "William",
            "David", "Richard", "Charles", "Joseph", "Thomas",
            "Christopher", "Daniel", "Paul", "Mark", "Donald",
            "George", "Kenneth", "Steven", "Edward", "Brian",
            "Ronald", "Anthony", "Kevin", "Jason", "Matthew",
            "Gary", "Timothy", "Jose", "Larry", "Jeffrey",
            "Frank", "Scott", "Eric", "Stephen", "Andrew",
            "Raymond", "Gregory", "Joshua", "Jerry", "Dennis",
            "Walter", "Patrick", "Peter", "Harold", "Douglas",
            "Henry", "Carl", "Arthur", "Ryan", "Roger"
        ));;
		stringFinderService.findStrings("http://norvig.com/big.txt", stringsToFind);
	}

}
