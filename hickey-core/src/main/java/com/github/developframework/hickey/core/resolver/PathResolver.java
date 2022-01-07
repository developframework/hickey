package com.github.developframework.hickey.core.resolver;

import com.github.developframework.hickey.core.MethodParameterResolver;
import com.github.developframework.hickey.core.annotations.Path;
import com.github.developframework.hickey.core.structs.ContentType;
import com.github.developframework.hickey.core.structs.MethodParameterMetadata;
import com.github.developframework.hickey.core.structs.RequestWrapper;

/**
 * @author qiushui on 2022-01-07.
 */
public final class PathResolver implements MethodParameterResolver {

    @Override
    public boolean matches(MethodParameterMetadata metadata, ContentType contentType) {
        return metadata.hasAnnotation(Path.class);
    }

    @Override
    public void assemble(MethodParameterMetadata metadata, RequestWrapper requestWrapper) {
        final Path path = metadata.getAnnotation(Path.class);
        final Object value = metadata.getValue();
        requestWrapper.setUrl(
                requestWrapper.getUrl().replace("{" + path.value() + "}", value == null ? "" : value.toString())
        );
    }
}
