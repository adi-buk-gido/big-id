package com.example.bigid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.bigid.service.StringFinderService;


public class MainApplication {

	    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

	public static void main(String[] args) throws Exception {

		List<String> targetWords = new ArrayList<>(Arrays.asList(
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

        List<String> stringsToFind2 = new ArrayList<>(Arrays.asList(
            "James", "John"
        ));;

        StringFinderService stringFinderService = new StringFinderService();
		stringFinderService.findStrings("http://norvig.com/big.txt", targetWords);
	}

}
