package org.example.exceptions;

public class InvalidGameConfigException extends RuntimeException {
    public InvalidGameConfigException(String msg) {
        super(msg);
    }
}
