package com.example.bigid.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValueLocation {

    public int lineOffset;
    public int charOffset;

}
