package com.example.bigid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.bigid.service.StringFinderService;

public class MainApplication {
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
                "Henry", "Carl", "Arthur", "Ryan", "Roger"));
        ;

        String url = "http://norvig.com/big.txt";
        
        StringFinderService stringFinderService = new StringFinderService();
        stringFinderService.findStrings(url, targetWords);
    }

}
