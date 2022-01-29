package com.github.developframework.hickey.core.structs.responsebody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author qiushui on 2022-01-11.
 */
public final class BinarayResponseBody extends ResponseBody<InputStream> {

    public BinarayResponseBody(InputStream inputStream, Charset charset) throws IOException {
        super(inputStream, charset);
    }

    @Override
    public InputStream getBody() {
        return inputStream;
    }

    @Override
    public String pretty() {
        return "(Binary byte data)";
    }
}
