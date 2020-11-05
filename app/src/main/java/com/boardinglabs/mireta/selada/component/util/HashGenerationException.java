package com.boardinglabs.mireta.selada.component.util;

/**
 * Created by Dhimas on 10/9/17.
 */

public class HashGenerationException extends Exception {
    private static final long serialVersionUID = 1L;

    public HashGenerationException() {
        super();
    }

    public HashGenerationException(String message) {
        super(message);
    }

    public HashGenerationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
