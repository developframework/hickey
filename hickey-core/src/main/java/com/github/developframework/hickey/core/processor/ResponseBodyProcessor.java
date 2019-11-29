package com.github.developframework.hickey.core.processor;

public interface ResponseBodyProcessor<T> {

    T process(byte[] data);
}
