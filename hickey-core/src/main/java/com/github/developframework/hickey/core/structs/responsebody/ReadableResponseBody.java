package com.github.developframework.hickey.core.structs.responsebody;

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
public final class ReadableResponseBody extends ResponseBody<String> {

    @Getter
    private final String cache;

    public ReadableResponseBody(InputStream inputStream, Charset charset) throws IOException {
        super(inputStream, charset);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            inputStream.transferTo(os);
            cache = os.toString(charset);
        }
    }

    @Override
    public String getBody() {
        return cache;
    }

    @Override
    public String pretty() {
        return cache;
    }
}
