package org.example.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Request {
    private String controller;
    private String method;
    private String data;
}
