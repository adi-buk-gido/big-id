package com.example.bigid.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.bigid.data.ValueLocation;

public class MatcherService {

    private final Logger logger = LoggerFactory.getLogger(MatcherService.class);

    private ConcurrentHashMap<String, List<ValueLocation>> stringLocation = new ConcurrentHashMap<>();

    public void matchBatch(List<String> batch, int batchStartingLine, Set<String> lowercaseTargetWords,
            Map<String, String> lowercaseToOriginal) throws Exception {
        logger.info("Processing batch to match strings in starting line: {}", batchStartingLine);
        // Save in the map only the relevant
        for (int i = 0; i < batch.size(); i++) {
            String originalLine = batch.get(i);
            // Split by spaces
            String[] words = originalLine.split("\\s+");

            int searchStart = 0;
            for (String word : words) {
                if (word.isEmpty()) {
                    continue;
                }

                // Keep track of current word charOffset
                int charOffset = indexOfWord(originalLine, word, searchStart);
                // Clear the word from special chars
                String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "");

                if (lowercaseTargetWords.contains(cleanWord)) {
                    String originalInputWord = lowercaseToOriginal.get(cleanWord);
                    stringLocation
                            .computeIfAbsent(originalInputWord, k -> Collections.synchronizedList(new ArrayList<>()))
                            .add(new ValueLocation(batchStartingLine + i, charOffset));
                }
                // Continue the search start with the new charOffset of the new word
                searchStart = charOffset + word.length();
            }
        }

    }

    private int indexOfWord(String line, String word, int fromIndex) {
        return line.indexOf(word, fromIndex);
    }

    public ConcurrentHashMap<String, List<ValueLocation>> getStringLocation() {
        return stringLocation;
    }

}
