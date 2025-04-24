package com.example.lemonade.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.example.lemonade.consts.RestConsts;
import com.example.lemonade.dto.WordCountRequest;
import com.example.lemonade.service.WordsCounterService;
import com.example.lemonade.dto.LemonadeResponseDto;


@RestController
@RequestMapping(RestConsts.Words.ROOT)
public class WordsController {

    private static final Logger logger = LoggerFactory.getLogger(WordsController.class);

    @Autowired
    WordsCounterService wordsCounterService;


    @GetMapping("{word}")
    public ResponseEntity<Integer> getNumberOfWords(@PathVariable(value = "word") String word){
        //logs
        Integer numOfOwrds = wordsCounterService.getNumberOfWords(word);
        return new ResponseEntity<>(numOfOwrds, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<HttpStatus> countWords(@Valid @RequestBody WordCountRequest wordCountRequest) throws Exception{
        logger.info("Get request to count words for URL: " + wordCountRequest.getUrl());
        wordsCounterService.countWords(wordCountRequest.getUrl());
        //logger
        return new ResponseEntity<>(HttpStatus.CREATED);
    }



}
