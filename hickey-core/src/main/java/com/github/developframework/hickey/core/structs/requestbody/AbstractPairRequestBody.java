package com.github.developframework.hickey.core.structs.requestbody;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author qiushui on 2022-01-29.
 */
public abstract class AbstractPairRequestBody implements RequestBody {

    public abstract void addPair(String name, Object value);

    public final void addPairs(Object pairEntity) {
        if (pairEntity instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) pairEntity;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    addPair(entry.getKey(), entry.getValue());
                }
            }
        } else {
            for (Field field : pairEntity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    addPair(field.getName(), field.get(pairEntity));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
