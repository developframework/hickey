package com.github.developframework.hickey.core.structs;

import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 可读文本响应内容
 *
 * @author qiushui on 2022-01-11.
 */
public class ReadableResponseBody extends ResponseBody<String> {

    @Getter
    private final String readable;

    public ReadableResponseBody(InputStream inputStream, Charset charset) throws IOException {
        super(inputStream, charset);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            inputStream.transferTo(os);
            readable = os.toString(charset);
        }
    }

    @Override
    protected String getBody() {
        return readable;
    }

    @Override
    public String pretty() {
        return readable;
    }
}
