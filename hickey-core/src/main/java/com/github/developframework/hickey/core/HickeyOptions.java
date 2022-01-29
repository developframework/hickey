package com.github.developframework.hickey.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.developframework.hickey.core.structs.MimeType;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qiushui on 2021-12-31.
 */
public final class HickeyOptions {

    // 默认编码
    public static final String KEY_DEFAULT_CHARSET = "default-charset";

    // jackson的ObjectMapper
    public static final String KEY_JACKSON_OBJECTMAPPER = "jackson-objectmapper";

    // 除了GET请求外 其它方式默认Content-Type值
    public static final String KEY_DEFAULT_CONTENT_TYPE = "default-content-type";

    // 默认连接超时时间
    public static final String KEY_DEFAULT_CONNECTION_TIMEOUT = "default-connection-timeout";

    // 默认读取超时时间
    public static final String KEY_DEFAULT_READ_TIMEOUT = "default-read-timeout";

    private final Map<String, Object> options = new HashMap<>();

    public HickeyOptions() {
        options.put(KEY_DEFAULT_CHARSET, StandardCharsets.UTF_8);
        options.put(KEY_DEFAULT_CONTENT_TYPE, MimeType.APPLICATION_JSON);
        options.put(KEY_DEFAULT_CONNECTION_TIMEOUT, Duration.ofSeconds(10L));
        options.put(KEY_DEFAULT_READ_TIMEOUT, Duration.ofSeconds(10L));
        options.put(KEY_JACKSON_OBJECTMAPPER, new ObjectMapper());
    }

    @SuppressWarnings("unchecked")
    public <T> T readOption(String key, Class<T> tClass) {
        return (T) options.get(key);
    }
}
