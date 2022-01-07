package com.github.developframework.hickey.core.structs;

import java.nio.charset.Charset;

/**
 * 空的请求体内容
 *
 * @author qiushui on 2022-01-06.
 */
public final class EmptyRequestBody implements RequestBody {

    public static final String EMPTY_PRETTY = "(empty)";

    @Override
    public byte[] toByteArray(Charset charset) {
        return null;
    }

    @Override
    public String pretty() {
        return EMPTY_PRETTY;
    }
}
