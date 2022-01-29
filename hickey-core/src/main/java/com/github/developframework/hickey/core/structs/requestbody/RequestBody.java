package com.github.developframework.hickey.core.structs.requestbody;

import java.nio.charset.Charset;

/**
 * 请求体内容
 *
 * @author qiushui on 2022-01-06.
 */
public interface RequestBody {

    /**
     * 写出字节数据
     */
    byte[] toByteArray(Charset charset);

    /**
     * 打印时如何显示
     */
    String pretty();
}
