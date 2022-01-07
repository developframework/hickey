package com.github.developframework.hickey.core.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.developframework.hickey.core.MethodParameterResolver;
import com.github.developframework.hickey.core.annotations.PairMap;
import com.github.developframework.hickey.core.structs.ContentType;
import com.github.developframework.hickey.core.structs.JsonRequestBody;
import com.github.developframework.hickey.core.structs.MethodParameterMetadata;
import com.github.developframework.hickey.core.structs.RequestWrapper;
import lombok.RequiredArgsConstructor;

/**
 * @author qiushui on 2022-01-07.
 */
@RequiredArgsConstructor
public class JsonPairMapResolver implements MethodParameterResolver {

    private final ObjectMapper objectMapper;

    @Override
    public boolean matches(MethodParameterMetadata metadata, ContentType contentType) {
        return metadata.hasAnnotation(PairMap.class) && contentType == ContentType.APPLICATION_JSON;
    }

    @Override
    public void assemble(MethodParameterMetadata metadata, RequestWrapper requestWrapper) {
        JsonRequestBody requestBody = (JsonRequestBody) requestWrapper.getRequestBody();
        if (requestBody == null) {
            requestBody = new JsonRequestBody(objectMapper);
            requestWrapper.setRequestBody(requestBody);
        }
        requestBody.addPairMap(metadata.getValue());
    }
}
