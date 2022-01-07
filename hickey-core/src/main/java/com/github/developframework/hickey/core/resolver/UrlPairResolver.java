package com.github.developframework.hickey.core.resolver;

import com.github.developframework.hickey.core.MethodParameterResolver;
import com.github.developframework.hickey.core.annotations.UrlPair;
import com.github.developframework.hickey.core.structs.ContentType;
import com.github.developframework.hickey.core.structs.MethodParameterMetadata;
import com.github.developframework.hickey.core.structs.RequestWrapper;

/**
 * @author qiushui on 2022-01-06.
 */
public final class UrlPairResolver implements MethodParameterResolver {

    @Override
    public boolean matches(MethodParameterMetadata metadata, ContentType contentType) {
        return metadata.hasAnnotation(UrlPair.class);
    }

    @Override
    public void assemble(MethodParameterMetadata metadata, RequestWrapper requestWrapper) {
        final UrlPair urlPair = metadata.getAnnotation(UrlPair.class);
        final Object value = metadata.getValue();
        requestWrapper
                .getParameters()
                .put(
                        urlPair.value(),
                        value == null ? urlPair.defaultValue() : value.toString()
                );
    }
}
