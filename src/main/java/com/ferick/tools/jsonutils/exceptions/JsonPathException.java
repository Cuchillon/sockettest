package com.ferick.tools.jsonutils.exceptions;

public class JsonPathException extends RuntimeException {

    private static final String MESSAGE = "JSON path is incorrect!";

    public JsonPathException() {
        super(MESSAGE);
    }

    public JsonPathException(String message) {
        super(message);
    }
}
