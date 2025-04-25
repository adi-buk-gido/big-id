package com.example.bigid.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileReaderService{

    //Can be taken out to a property to be controlled from outside
    private static final int BATCH_SIZE = 1000;
    private static final int THREAD_POOL_SIZE = 4;

    private static final Logger logger = LoggerFactory.getLogger(FileReaderService.class);
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    @Autowired
    MatcherService matcherService;


    /**
     * Reads the file and calls matcher service
     * @param url
     * @param stringsToFindLC
     * @throws IOException
     * @throws InterruptedException
     */
    public void readFile(String url, List<String> stringsToFindLC) throws IOException, InterruptedException {
        logger.debug("Opening connection with URL:{}", url);
        URL urlToSearch = new URL(url);
        URLConnection connection = urlToSearch.openConnection();

        logger.debug("Reading URL data in batches of: {}", BATCH_SIZE);
        InputStream openStream = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(openStream);
        BufferedReader buffReader = new BufferedReader(reader);
        List<String> batch = new ArrayList<>();
        String line;
        int lineNum = 0;

        List<Future<Boolean>> futures = new ArrayList<>();
            while ((line = buffReader.readLine()) != null) {
                line.toLowerCase();
                batch.add(line);

                if (batch.size() == BATCH_SIZE) {
                    //Passing a copy so it doesnt get cleared
                    List<String> batchCopy = new ArrayList<>(batch);
                    int batchStartingLine = lineNum - BATCH_SIZE + 1;
                    futures.add(executor.submit(() -> {matcherService.matchBatch(batchCopy, batchStartingLine, stringsToFindLC); return true;}));
                    batch.clear();
                }
                lineNum++;

            // Process any remaining lines
            if (!batch.isEmpty()) {
                List<String> batchCopy = new ArrayList<>(batch);
                int batchStartingLine = lineNum - batch.size();
                futures.add(executor.submit(() ->  {matcherService.matchBatch(batchCopy, batchStartingLine, stringsToFindLC); return true;}));
            }

        }

        List<Boolean> allResults = new ArrayList<>();
        for (Future<Boolean> future : futures) {
            try {
                allResults.add(future.get()); // blocks until task is done
            } catch (Exception e) {
                System.err.println("Batch failed: " + e.getMessage());
            }
        }

        if(allResults.contains(false)){
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
