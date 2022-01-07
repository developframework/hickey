package com.github.developframework.hickey.core.resolver;

import com.github.developframework.hickey.core.MethodParameterResolver;
import com.github.developframework.hickey.core.annotations.Pair;
import com.github.developframework.hickey.core.structs.ContentType;
import com.github.developframework.hickey.core.structs.FormUrlencodedBody;
import com.github.developframework.hickey.core.structs.MethodParameterMetadata;
import com.github.developframework.hickey.core.structs.RequestWrapper;

/**
 * @author qiushui on 2022-01-06.
 */
public final class FormUrlencodedPairResolver implements MethodParameterResolver {

    @Override
    public boolean matches(MethodParameterMetadata metadata, ContentType contentType) {
        return metadata.hasAnnotation(Pair.class) && contentType == ContentType.FORM_URLENCODED;
    }

    @Override
    public void assemble(MethodParameterMetadata metadata, RequestWrapper requestWrapper) {
        FormUrlencodedBody requestBody = (FormUrlencodedBody) requestWrapper.getRequestBody();
        if (requestBody == null) {
            requestBody = new FormUrlencodedBody();
            requestWrapper.setRequestBody(requestBody);
        }
        final Pair pair = metadata.getAnnotation(Pair.class);
        requestBody.addPair(pair.value(), metadata.getValue());
    }
}
