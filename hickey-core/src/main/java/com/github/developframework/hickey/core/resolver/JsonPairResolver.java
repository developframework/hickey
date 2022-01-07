package com.github.developframework.hickey.core.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.developframework.hickey.core.MethodParameterResolver;
import com.github.developframework.hickey.core.annotations.Pair;
import com.github.developframework.hickey.core.structs.ContentType;
import com.github.developframework.hickey.core.structs.JsonRequestBody;
import com.github.developframework.hickey.core.structs.MethodParameterMetadata;
import com.github.developframework.hickey.core.structs.RequestWrapper;
import lombok.RequiredArgsConstructor;

/**
 * @author qiushui on 2022-01-07.
 */
@RequiredArgsConstructor
public class JsonPairResolver implements MethodParameterResolver {

    private final ObjectMapper objectMapper;

    @Override
    public boolean matches(MethodParameterMetadata metadata, ContentType contentType) {
        return metadata.hasAnnotation(Pair.class) && contentType == ContentType.APPLICATION_JSON;
    }

    @Override
    public void assemble(MethodParameterMetadata metadata, RequestWrapper requestWrapper) {
        JsonRequestBody requestBody = (JsonRequestBody) requestWrapper.getRequestBody();
        if (requestBody == null) {
            requestBody = new JsonRequestBody(objectMapper);
            requestWrapper.setRequestBody(requestBody);
        }
        final Pair pair = metadata.getAnnotation(Pair.class);
        requestBody.addPair(pair.value(), metadata.getValue());
    }
}
