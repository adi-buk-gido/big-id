package com.example.bigid.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StringFinderServiceTest {

    private FileReaderService fileReaderService;
    private AggregatorService aggregatorService;
    private StringFinderService stringFinderService;

    @BeforeEach
    public void setUp() {
        fileReaderService = mock(FileReaderService.class);
        aggregatorService = mock(AggregatorService.class);
        stringFinderService = new StringFinderService(fileReaderService, aggregatorService);
    }

    @Test
    public void testValidUrlAndTargetWords() throws Exception {
        String validUrl = "https://example.com/data.txt";
        List<String> targetWords = Arrays.asList("James", "Robert");

        stringFinderService.findStrings(validUrl, targetWords);

        verify(fileReaderService, times(1)).readFile(eq(validUrl), anySet(), anyMap());
        verify(aggregatorService, times(1)).aggregateResults();
    }

    @Test
    public void testInvalidUrlThrowsException() throws Exception {
        String invalidUrl = "invalid-url";
        List<String> targetWords = Arrays.asList("James");

        Exception exception = assertThrows(Exception.class, () -> {
            stringFinderService.findStrings(invalidUrl, targetWords);
        });

        String expectedMessage = "URL provided not valid";
        assertTrue(exception.getMessage().contains(expectedMessage));

        verify(fileReaderService, never()).readFile(anyString(), anySet(), anyMap());
        verify(aggregatorService, never()).aggregateResults();
    }

    @Test
    public void testEmptyTargetWordsList() throws Exception {
        String validUrl = "https://example.com/data.txt";
        List<String> emptyTargetWords = Collections.emptyList();

        stringFinderService.findStrings(validUrl, emptyTargetWords);

        verify(fileReaderService, never()).readFile(anyString(), anySet(), anyMap());
        verify(aggregatorService, never()).aggregateResults();
    }

    @Test
    public void testNullTargetWordsList() throws Exception {
        String validUrl = "https://example.com/data.txt";

        stringFinderService.findStrings(validUrl, null);

        verify(fileReaderService, never()).readFile(anyString(), anySet(), anyMap());
        verify(aggregatorService, never()).aggregateResults();
    }
}
