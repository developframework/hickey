package com.github.developframework.hickey.core.structs;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qiushui on 2022-01-06.
 */
public final class FormUrlencodedBody implements RequestBody {

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

    public void addPair(String name, Object value) {
        pairs.put(name, value == null ? "" : value.toString());
    }

    @SuppressWarnings("unchecked")
    public void addPairMap(Object value) {
        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    addPair(entry.getKey(), entry.getValue());
                }
            }
        } else {
            for (Field field : value.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    final Object v = field.get(value);
                    addPair(field.getName(), v);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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
