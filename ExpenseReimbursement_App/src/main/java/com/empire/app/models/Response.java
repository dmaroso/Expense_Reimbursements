package com.empire.app.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    private String type;
    private String message;
    private Object body;
}
