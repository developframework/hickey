package com.github.developframework.hickey.core.structs.requestbody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author qiushui on 2022-01-07.
 */
public class JsonRequestBody extends AbstractPairRequestBody {

    private final ObjectMapper objectMapper;

    private final JsonNode rootNode;

    private String cacheJson;

    public JsonRequestBody(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.rootNode = objectMapper.createObjectNode();
    }

    @Override
    public void addPair(String key, Object value) {
        ObjectNode objectNode = (ObjectNode) rootNode;
        if (value == null) {
            objectNode.putNull(key);
        } else {
            objectNode.put(key, value.toString());
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
