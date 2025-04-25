package com.example.bigid.service;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StringFinderService {

    private static final Logger logger = LoggerFactory.getLogger(StringFinderService.class);

    @Autowired
    FileReaderService fileReaderService;

    @Autowired
    AggregatorService aggregatorService;

    public void findStrings(String url, List<String> stringsToFind) throws Exception{
        logger.debug("Finding strings for URL: {}, strings to find: {} ", url, stringsToFind);
        ValidateURL(url);
        boolean isListValid = validateStringsToFind(stringsToFind);
        if(!isListValid){
            System.out.println("No words found to search");
            return;
        }
        logger.debug("Lower casing string to find..");
        List<String> stringsToFindLC = stringsToFind.stream().map(String::toLowerCase).collect(Collectors.toList());
        fileReaderService.readFile(url, stringsToFindLC);

        logger.debug("Aggregating results");
        aggregatorService.aggregateResults();

    }

    private boolean validateStringsToFind(List<String> stringsToFind) {
        logger.debug("Validating string to find: {}", stringsToFind);
       if(stringsToFind == null || stringsToFind.isEmpty()){
            logger.warn("No strings provided to search, no processing will be done");
            return false;
       }
       return true;
    }

    private void ValidateURL(String url) throws Exception {
        logger.debug("Validation URL: {}", url);
        boolean isValidURL = UrlValidator.getInstance().isValid(url);
        if(!isValidURL){
            logger.error("URL provided: {} isnt valid, no processing will be done", url);
            throw new Exception("URL provided not valid");
        }


    }



}
