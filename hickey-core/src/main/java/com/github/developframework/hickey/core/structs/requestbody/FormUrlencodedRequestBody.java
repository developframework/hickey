package com.github.developframework.hickey.core.structs.requestbody;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qiushui on 2022-01-06.
 */
public final class FormUrlencodedRequestBody extends AbstractPairRequestBody {

    private final Map<String, String> pairs = new HashMap<>();

    private String cacheBody;

    @Override
    public byte[] toByteArray(Charset charset) {
        cacheBody = urlParametersFormat();
        return cacheBody == null ? null : cacheBody.getBytes();
    }

    @Override
    public String pretty() {
        return cacheBody == null ? EmptyRequestBody.EMPTY_PRETTY : cacheBody;
    }

    @Override
    public void addPair(String name, Object value) {
        pairs.put(name, value == null ? "" : value.toString());
    }

    /**
     * 处理成url参数格式
     */
    private String urlParametersFormat() {
        if (pairs.isEmpty()) {
            return null;
        }
        return pairs
                .entrySet()
                .stream()
                .filter(kv -> kv.getValue() != null)
                .map(kv -> String.format("%s=%s", kv.getKey(), URLEncoder.encode(kv.getValue(), StandardCharsets.UTF_8)))
                .collect(Collectors.joining("&"));
    }
}
