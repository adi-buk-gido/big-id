package com.example.bigid.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class WordsCounterServiceImpl implements WordsCounterService{

    private Map<String, Integer> wordsMap = new HashMap<>();


    @Override
    public void countWords(String url) throws IOException {
        // Validate URL validity

        URL urlToSearch = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlToSearch.openConnection();
        conn.setRequestMethod("GET");

        InputStream openStream = urlToSearch.openStream();

        InputStreamReader reader = new InputStreamReader(openStream);
        BufferedReader in = new BufferedReader(reader);

        String read;
        while ((read = in.readLine()) != null) {
                read = read.toLowerCase();
                read = read.replace('-', ' ');
                String[] readWords = read.split(" ", 0);
                for (String word : readWords) {
                    Integer wordCount = wordsMap.get(word);
                    if (wordCount != null){
                        wordsMap.put(word, wordCount + 1);
                    } else {
                        wordsMap.put(word, 1);
                    }
                }
        }
    }


    @Override
    public Integer getNumberOfWords(String word) {
        //logs
        Integer numOfWords = wordsMap.get(word.toLowerCase());
        return numOfWords == null ? 0 : numOfWords;
    }
    

}
