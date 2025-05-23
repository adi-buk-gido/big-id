package com.example.bigid.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.bigid.data.ValueLocation;

public class AggregatorService {

    private final Logger logger = LoggerFactory.getLogger(AggregatorService.class);

    private final MatcherService matcherService;

    public AggregatorService(MatcherService matcherService) {
        this.matcherService = matcherService;
    }

    public void aggregateResults() throws Exception {
        logger.info("Aggregate and print results");
        ConcurrentHashMap<String, List<ValueLocation>> results = matcherService.getStringLocation();
        for (Map.Entry<String, List<ValueLocation>> entry : results.entrySet()) {
            String matchedWord = entry.getKey(); // The search word (lowercased)
            List<ValueLocation> locations = entry.getValue();

            System.out.print("Word: '" + matchedWord + "' found at: ");

            for (int i = 0; i < locations.size(); i++) {
                ValueLocation loc = locations.get(i);
                System.out.print("[line " + loc.lineOffset + ", char " + loc.charOffset + "]");
                if (i < locations.size() - 1) {
                    System.out.print(", ");
                }
            }

            System.out.println(); // Move to next line after all locations for a word
        }
    }

}
