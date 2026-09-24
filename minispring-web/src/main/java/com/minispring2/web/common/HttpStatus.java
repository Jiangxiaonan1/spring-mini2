package com.minispring2.web.common;

public enum HttpStatus {
    OK(200),
    WRONG_PARAM(400),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405);

    private final int value;

    HttpStatus(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}