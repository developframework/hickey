package com.github.developframework.hickey.core.structs;

import com.github.developframework.hickey.core.structs.requestbody.RequestBody;
import lombok.Getter;
import lombok.Setter;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 请求体封装包
 *
 * @author qiushui on 2021-12-30.
 */
@Getter
@Setter
public final class RequestWrapper {

    private String label;

    private HttpMethod method;

    private String url;

    private String fullUrl;

    private Duration connectionTimeout;

    private Duration readTimeout;

    private Map<String, String> headers = new LinkedHashMap<>();

    private Map<String, String> parameters = new LinkedHashMap<>();

    private Charset charset;

    private RequestBody requestBody;

    private LocalDateTime beginTimestamp = LocalDateTime.now();

    public String getFullUrl() {
        if (fullUrl == null) {
            if (parameters.isEmpty()) {
                fullUrl = url;
            } else {
                fullUrl = url + "?" + parameters
                        .entrySet()
                        .stream()
                        .filter(kv -> kv.getValue() != null)
                        .map(kv -> String.format("%s=%s", kv.getKey(), URLEncoder.encode(kv.getValue(), charset)))
                        .collect(Collectors.joining("&"));
            }
        }
        return fullUrl;
    }
}
