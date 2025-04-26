package com.example.bigid.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReaderService{

    //Can be taken out to a property to be controlled from outside
    private static final int BATCH_SIZE = 5000;

    private static final Logger logger = LoggerFactory.getLogger(FileReaderService.class);
    private static int cores = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = Executors.newFixedThreadPool(cores * 2);

    MatcherService matcherService = new MatcherService();


    /**
     * Reads the file and calls matcher service
     * @param url                       The URL to search the words in
     * @param lowercaseTargetWords      Lowercase target words to search for
     * @throws IOException
     * @throws InterruptedException
     */
    public void readFile(String url, Set<String> lowercaseTargetWords, Map<String, String> lowercaseToOriginalTargetWords) throws IOException, InterruptedException {
        logger.debug("Opening connection with URL:{}", url);
        URL urlToSearch = new URL(url);
        URLConnection connection = urlToSearch.openConnection();

        logger.debug("Reading URL data in batches of: {}", BATCH_SIZE);
        InputStream openStream = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(openStream);
        BufferedReader buffReader = new BufferedReader(reader);
        List<String> batch = new ArrayList<>();
        String currentLineContent;
        int currentLineNumber = 0;

        List<Future<Boolean>> futures = new ArrayList<>();
        while ((currentLineContent = buffReader.readLine()) != null) {
                currentLineContent = currentLineContent.toLowerCase();
                batch.add(currentLineContent);

                if (batch.size() == BATCH_SIZE) {
                    //Passing a copy so it doesnt get cleared
                    List<String> batchCopy = new ArrayList<>(batch);
                    int batchStartingLine = currentLineNumber - BATCH_SIZE + 1;
                    futures.add(executor.submit(() -> {matcherService.matchBatch(batchCopy, batchStartingLine, lowercaseTargetWords, lowercaseToOriginalTargetWords); return true;}));
                    batch.clear();
                }
                currentLineNumber++;
        }

            // Process any remaining lines
            if (!batch.isEmpty()) {
                List<String> batchCopy = new ArrayList<>(batch);
                int batchStartingLine = currentLineNumber - batch.size();
                futures.add(executor.submit(() ->  {matcherService.matchBatch(batchCopy, batchStartingLine, lowercaseTargetWords, lowercaseToOriginalTargetWords); return true;}));
            }

        List<Boolean> allMatchersResults = new ArrayList<>();
        for (Future<Boolean> future : futures) {
            try {
                allMatchersResults.add(future.get()); // blocks until task is done
            } catch (Exception e) {
                System.err.println("Batch failed: " + e.getMessage());
            }
        }

        if(allMatchersResults.contains(false)){
            logger.info("Some matchers failed to process, partial data available");
        } else {
            logger.info("All matches completed succeddfuly");
        }
    }


    public static void shutdownAndAwait() throws InterruptedException{
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
    }

    

}
