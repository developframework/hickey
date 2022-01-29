package com.github.developframework.hickey.core.structs.responsebody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author qiushui on 2022-01-11.
 */
public abstract class ResponseBody<T> {

    protected final InputStream inputStream;

    protected final Charset charset;

    public ResponseBody(InputStream inputStream, Charset charset) throws IOException {
        this.inputStream = inputStream;
        this.charset = charset;
    }

    public abstract T getBody();

    public abstract String pretty();
}
