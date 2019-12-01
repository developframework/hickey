package com.github.developframework.hickey.core.exception;

import lombok.Getter;

/**
 * @author qiushui on 2019-12-01.
 */
public class TouchErrorException extends Exception {

    @Getter
    private Object errorObject;

    public TouchErrorException(Object errorObject) {
        this.errorObject = errorObject;
    }
}
