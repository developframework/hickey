package com.github.developframework.hickey.core.resolver;

import com.github.developframework.hickey.core.MethodParameterResolver;
import com.github.developframework.hickey.core.annotations.UrlPairMap;
import com.github.developframework.hickey.core.structs.ContentType;
import com.github.developframework.hickey.core.structs.MethodParameterMetadata;
import com.github.developframework.hickey.core.structs.RequestWrapper;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author qiushui on 2022-01-06.
 */
public final class UrlPairMapResolver implements MethodParameterResolver {

    @Override
    public boolean matches(MethodParameterMetadata metadata, ContentType contentType) {
        return metadata.hasAnnotation(UrlPairMap.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void assemble(MethodParameterMetadata metadata, RequestWrapper requestWrapper) {
        final Map<String, String> parameters = requestWrapper.getParameters();
        final Object value = metadata.getValue();
        if (value instanceof Map) {
            ((Map) value).forEach((k, v) -> {
                if (k != null) {
                    parameters.put(k.toString(), v == null ? "" : v.toString());
                }
            });
        } else {
            for (Field field : value.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    final Object v = field.get(value);
                    parameters.put(field.getName(), v == null ? "" : v.toString());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
