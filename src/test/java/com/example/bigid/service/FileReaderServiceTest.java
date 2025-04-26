package com.example.bigid.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FileReaderServiceTest {

    private FileReaderService fileReaderService;
    private MatcherService matcherServiceMock;

    @BeforeEach
    void setUp() {
        matcherServiceMock = mock(MatcherService.class);
        fileReaderService = new FileReaderService(matcherServiceMock);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        fileReaderService.shutdownAndAwait();
    }

    @Test
    void testReadFile_singleBatch_success() throws Exception {
        // Arrange
        String fileContent = "hello world\nthis is a test\nhello again";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());

        FileReaderService fileReaderSpy = Mockito.spy(fileReaderService);
        doReturn(inputStream).when(fileReaderSpy).openStream(any(URL.class));

        Set<String> lowercaseTargetWords = new HashSet<>(Arrays.asList("hello", "test"));
        Map<String, String> lowercaseToOriginal = new HashMap<>();
        lowercaseToOriginal.put("hello", "Hello");
        lowercaseToOriginal.put("test", "Test");

        // Act
        fileReaderSpy.readFile("http://dummy", lowercaseTargetWords, lowercaseToOriginal);

        // Assert
        verify(matcherServiceMock, atLeastOnce()).matchBatch(anyList(), anyInt(), eq(lowercaseTargetWords),
                eq(lowercaseToOriginal));
    }

    @Test
    void testReadFile_multipleBatches_success() throws Exception {
        // Arrange
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6000; i++) {
            sb.append("line ").append(i).append("\n");
        }
        InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());

        FileReaderService fileReaderSpy = Mockito.spy(fileReaderService);
        doReturn(inputStream).when(fileReaderSpy).openStream(any(URL.class));

        Set<String> lowercaseTargetWords = new HashSet<>(Collections.singletonList("line"));
        Map<String, String> lowercaseToOriginal = new HashMap<>();

        // Act
        fileReaderSpy.readFile("http://dummy", lowercaseTargetWords, lowercaseToOriginal);

        // Assert: Should call matcher more than once because 6000 lines > batch size
        // (5000)
        verify(matcherServiceMock, atLeast(2)).matchBatch(anyList(), anyInt(), eq(lowercaseTargetWords),
                eq(lowercaseToOriginal));
    }

    @Test
    void testReadFile_matcherFails_logsPartialData() throws Exception {
        // Arrange
        String fileContent = "hello world\nthis is a test\nhello again";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());

        FileReaderService fileReaderSpy = Mockito.spy(fileReaderService);
        doReturn(inputStream).when(fileReaderSpy).openStream(any(URL.class));

        Set<String> lowercaseTargetWords = new HashSet<>(Arrays.asList("hello", "test"));
        Map<String, String> lowercaseToOriginal = new HashMap<>();
        lowercaseToOriginal.put("hello", "Hello");
        lowercaseToOriginal.put("test", "Test");

        // Simulate matcherService throwing an exception on one batch
        doThrow(new RuntimeException("Simulated batch failure"))
                .when(matcherServiceMock)
                .matchBatch(anyList(), anyInt(), eq(lowercaseTargetWords), eq(lowercaseToOriginal));

        // Act & Assert
        assertDoesNotThrow(() -> {
            fileReaderSpy.readFile("http://dummy", lowercaseTargetWords, lowercaseToOriginal);
        });

        // You could verify that matcherService.matchBatch() was called
        verify(matcherServiceMock, atLeastOnce()).matchBatch(anyList(), anyInt(), eq(lowercaseTargetWords),
                eq(lowercaseToOriginal));
    }

    @Test
    void testReadFile_mixedSuccessAndFailureInMatchers() throws Exception {
        // Arrange
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6000; i++) {
            sb.append("line ").append(i).append("\n");
        }
        InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());

        FileReaderService fileReaderSpy = Mockito.spy(fileReaderService);
        doReturn(inputStream).when(fileReaderSpy).openStream(any(URL.class));

        Set<String> lowercaseTargetWords = new HashSet<>(Collections.singletonList("line"));
        Map<String, String> lowercaseToOriginal = new HashMap<>();

        // Simulate:
        // - First matcherService.matchBatch() call throws an exception
        // - Second matcherService.matchBatch() call succeeds normally
        doThrow(new RuntimeException("Simulated failure in first batch"))
                .doNothing() // second call succeeds
                .when(matcherServiceMock)
                .matchBatch(anyList(), anyInt(), eq(lowercaseTargetWords), eq(lowercaseToOriginal));

        // Act & Assert
        assertDoesNotThrow(() -> {
            fileReaderSpy.readFile("http://dummy", lowercaseTargetWords, lowercaseToOriginal);
        });

        // Verify matcherService was called twice (6000 lines → 2 batches)
        verify(matcherServiceMock, times(2)).matchBatch(anyList(), anyInt(), eq(lowercaseTargetWords),
                eq(lowercaseToOriginal));
    }

    @Test
    void testShutdownAndAwait_success() throws Exception {
        // Just making sure no exception thrown
        fileReaderService.shutdownAndAwait();
    }
}
