package com.example.bigid.service;

import java.io.IOException;

public interface WordsCounterService {

    void countWords(String url) throws IOException;

    Integer getNumberOfWords(String word);

}
