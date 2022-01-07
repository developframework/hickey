package com.github.developframework.hickey.core;

/**
 * @author qiushui on 2022-01-06.
 */
public class HickeyException extends RuntimeException {

    public HickeyException(String message) {
        super(message);
    }

    public HickeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
