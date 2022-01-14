package com.github.developframework.hickey.core.structs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author qiushui on 2022-01-11.
 */
public class BinarayResponseBody extends ResponseBody<InputStream> {

    public BinarayResponseBody(InputStream inputStream, Charset charset) throws IOException {
        super(inputStream, charset);
    }

    @Override
    protected InputStream getBody() {
        return inputStream;
    }

    @Override
    protected boolean supportContentType(String contentTypeHeader) {
        return true;
    }

    @Override
    public String pretty() {
        return "(Binary byte data)";
    }
}
