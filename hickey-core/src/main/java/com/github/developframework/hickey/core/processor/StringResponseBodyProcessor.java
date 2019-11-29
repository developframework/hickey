package com.github.developframework.hickey.core.processor;

import java.nio.charset.StandardCharsets;

public class StringResponseBodyProcessor implements ResponseBodyProcessor<String> {

    @Override
    public String process(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }
}
