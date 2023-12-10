package org.example.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Request {
    private String controller;
    private String method;
    private String data;
}
