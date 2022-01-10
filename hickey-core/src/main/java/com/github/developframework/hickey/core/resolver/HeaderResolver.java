package com.github.developframework.hickey.core.resolver;

import com.github.developframework.hickey.core.MethodParameterResolver;
import com.github.developframework.hickey.core.annotations.Header;
import com.github.developframework.hickey.core.structs.ContentType;
import com.github.developframework.hickey.core.structs.MethodParameterMetadata;
import com.github.developframework.hickey.core.structs.RequestWrapper;

/**
 * 头信息解析器
 *
 * @author qiushui on 2022-01-10.
 */
public class HeaderResolver implements MethodParameterResolver {

    @Override
    public boolean matches(MethodParameterMetadata metadata, ContentType contentType) {
        return metadata.hasAnnotation(Header.class);
    }

    @Override
    public void assemble(MethodParameterMetadata metadata, RequestWrapper requestWrapper) {
        final Header header = metadata.getAnnotation(Header.class);
        final Object value = metadata.getValue();
        if (value != null) {
            requestWrapper.getHeaders().put(header.value(), value.toString());
        }
    }
}
