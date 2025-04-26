package com.example.bigid.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.bigid.data.ValueLocation;

public class MatcherServiceTest {

    private MatcherService matcherService;

    @BeforeEach
    public void setUp() {
        matcherService = new MatcherService();
    }

    @Test
    public void testSingleMatch() throws Exception {
        List<String> batch = Arrays.asList("hello james, how are you?");
        Set<String> lowercaseTargetWords = new HashSet<>(Arrays.asList("james"));
        Map<String, String> lowercaseToOriginal = new HashMap<>();
        lowercaseToOriginal.put("james", "James");

        matcherService.matchBatch(batch, 0, lowercaseTargetWords, lowercaseToOriginal);

        ConcurrentHashMap<String, List<ValueLocation>> results = matcherService.getStringLocation();
        assertTrue(results.containsKey("James"));
        assertEquals(1, results.get("James").size());

        ValueLocation location = results.get("James").get(0);
        assertEquals(0, location.getLineOffset()); // line number
        assertEquals(6, location.getCharOffset()); // character offset in line
    }

    @Test
    public void testMultipleMatchesSameLine() throws Exception {
        List<String> batch = Arrays.asList("james and james are friends with robert.");
        Set<String> lowercaseTargetWords = new HashSet<>(Arrays.asList("james", "robert"));
        Map<String, String> lowercaseToOriginal = new HashMap<>();
        lowercaseToOriginal.put("james", "James");
        lowercaseToOriginal.put("robert", "Robert");

        matcherService.matchBatch(batch, 0, lowercaseTargetWords, lowercaseToOriginal);

        ConcurrentHashMap<String, List<ValueLocation>> results = matcherService.getStringLocation();
        assertEquals(2, results.get("James").size());
        assertEquals(1, results.get("Robert").size());
    }

    @Test
    public void testNoMatch() throws Exception {
        List<String> batch = Arrays.asList("hello world, nothing interesting here.");
        Set<String> lowercaseTargetWords = new HashSet<>(Arrays.asList("james", "robert"));
        Map<String, String> lowercaseToOriginal = new HashMap<>();
        lowercaseToOriginal.put("james", "James");
        lowercaseToOriginal.put("robert", "Robert");

        matcherService.matchBatch(batch, 0, lowercaseTargetWords, lowercaseToOriginal);

        ConcurrentHashMap<String, List<ValueLocation>> results = matcherService.getStringLocation();
        assertTrue(results.isEmpty());
    }

    @Test
    public void testMatchWithSpecialCharacters() throws Exception {
        List<String> batch = Arrays.asList("hello, james! How's it going?");
        Set<String> lowercaseTargetWords = new HashSet<>(Arrays.asList("james"));
        Map<String, String> lowercaseToOriginal = new HashMap<>();
        lowercaseToOriginal.put("james", "James");

        matcherService.matchBatch(batch, 0, lowercaseTargetWords, lowercaseToOriginal);

        ConcurrentHashMap<String, List<ValueLocation>> results = matcherService.getStringLocation();
        assertTrue(results.containsKey("James"));
        assertEquals(1, results.get("James").size());

        ValueLocation location = results.get("James").get(0);
        assertEquals("James", lowercaseToOriginal.get("james")); // original casing
        assertEquals(7, location.getCharOffset()); // should find James after comma
    }

    @Test
    public void testMatchInMultipleLines() throws Exception {
        List<String> batch = Arrays.asList(
                "first line no match.",
                "second line with james.",
                "third line with robert.");

        Set<String> lowercaseTargetWords = new HashSet<>(Arrays.asList("james", "robert"));
        Map<String, String> lowercaseToOriginal = new HashMap<>();
        lowercaseToOriginal.put("james", "James");
        lowercaseToOriginal.put("robert", "Robert");

        matcherService.matchBatch(batch, 0, lowercaseTargetWords, lowercaseToOriginal);

        ConcurrentHashMap<String, List<ValueLocation>> results = matcherService.getStringLocation();
        assertEquals(1, results.get("James").size());
        assertEquals(1, results.get("Robert").size());

        ValueLocation jamesLocation = results.get("James").get(0);
        ValueLocation robertLocation = results.get("Robert").get(0);

        assertEquals(1, jamesLocation.getLineOffset()); // second line
        assertEquals(2, robertLocation.getLineOffset()); // third line
    }
}
