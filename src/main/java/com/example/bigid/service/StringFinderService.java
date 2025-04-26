package com.example.bigid.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StringFinderService {

    private static final Logger logger = LoggerFactory.getLogger(StringFinderService.class);

    FileReaderService fileReaderService = new FileReaderService();
    AggregatorService aggregatorService = new AggregatorService();


    /**
     * Finding target words in a URL. We call a file reader then match the results and then aggregate the response.
     * @param url               The URL to search for the words
     * @param targetWords       The target words to search for   
     * @throws Exception
     */
    public void findStrings(String url, List<String> targetWords) throws Exception{
        logger.debug("Finding strings for URL: {}, target words: {} ", url, targetWords);
        ValidateURL(url);
        boolean isListValid = validateTargetWords(targetWords);
        if(!isListValid){
            System.out.println("No words found to search");
            return;
        }

        logger.debug("Creating map of target words to lowecased");
        Set<String> lowercaseTargetWords = targetWords.stream()
        .map(String::toLowerCase)
        .collect(Collectors.toSet());

        Map<String, String> lowercaseToOriginalTargetWords = new HashMap<>();
        for (String word : targetWords) {
            lowercaseToOriginalTargetWords.put(word.toLowerCase(), word);
        }

        fileReaderService.readFile(url, lowercaseTargetWords, lowercaseToOriginalTargetWords);

        logger.debug("Aggregating results");
        aggregatorService.aggregateResults();

    }

    private boolean validateTargetWords(List<String> stringsToFind) {
        logger.debug("Validating target words: {}", stringsToFind);
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
