package com.github.developframework.hickey.core.structs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author qiushui on 2022-01-07.
 */
public class JsonRequestBody implements RequestBody {

    private final ObjectMapper objectMapper;

    private final JsonNode rootNode;

    private String cacheJson;

    public JsonRequestBody(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.rootNode = objectMapper.createObjectNode();
    }

    public void addPair(String key, Object value) {
        ObjectNode objectNode = (ObjectNode) rootNode;
        if (value == null) {
            objectNode.putNull(key);
        } else {
            objectNode.put(key, value.toString());
        }
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
                    addPair(field.getName(), v == null ? "" : v.toString());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public byte[] toByteArray(Charset charset) {
        try {
            cacheJson = objectMapper.writeValueAsString(rootNode);
            return cacheJson.getBytes(charset);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String pretty() {
        return cacheJson;
    }
}
