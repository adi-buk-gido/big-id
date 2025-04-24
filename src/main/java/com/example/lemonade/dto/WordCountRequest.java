package com.example.lemonade.dto;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

import lombok.Data;

@Data
public class WordCountRequest {

    @NotBlank (message = "URL is required")
    @URL
    String url;

}
