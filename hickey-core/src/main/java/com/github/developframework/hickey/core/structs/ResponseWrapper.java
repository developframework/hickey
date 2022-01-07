package com.github.developframework.hickey.core.structs;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author qiushui on 2021-12-30.
 */
@Getter
@Setter
public final class ResponseWrapper {

    // 状态码
    private int status;

    // 头信息
    private Map<String, List<String>> headers;

    // 响应输入流
    private InputStream responseBody;

    // 花费时间（毫秒）
    private long cost;

    // 连接错误
    private boolean connectError;

    // 连接超时
    private boolean connectTimeout;

    // 读取超时
    private boolean readTimeout;

    // 错误信息
    private String errorMessage;

    /**
     * 执行是否正确
     */
    public boolean executeOK() {
        return !connectError && !connectTimeout && !readTimeout;
    }
}
