package com.github.developframework.hickey.core.processor;

public interface ResponseBodyProcessor<T, Y> {

    T process(byte[] data);

    Y errorProcess(byte[] data);

    default boolean success(int httpStatus) {
        return httpStatus >= 200 && httpStatus < 300;
    }
}
