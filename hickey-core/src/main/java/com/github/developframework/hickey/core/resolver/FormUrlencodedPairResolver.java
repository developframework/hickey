package com.github.developframework.hickey.core.resolver;

import com.github.developframework.hickey.core.MethodParameterResolver;
import com.github.developframework.hickey.core.annotations.Pair;
import com.github.developframework.hickey.core.structs.MethodParameterMetadata;
import com.github.developframework.hickey.core.structs.MimeType;
import com.github.developframework.hickey.core.structs.PairEntity;
import com.github.developframework.hickey.core.structs.RequestWrapper;
import com.github.developframework.hickey.core.structs.requestbody.FormUrlencodedRequestBody;

import java.util.Map;

/**
 * @author qiushui on 2022-01-06.
 */
public final class FormUrlencodedPairResolver implements MethodParameterResolver {

    @Override
    public boolean matches(MethodParameterMetadata metadata, MimeType mimeType) {
        return mimeType == MimeType.FORM_URLENCODED;
    }

    @Override
    public void assemble(MethodParameterMetadata metadata, RequestWrapper requestWrapper) {
        FormUrlencodedRequestBody requestBody = (FormUrlencodedRequestBody) requestWrapper.getRequestBody();
        if (requestBody == null) {
            requestBody = new FormUrlencodedRequestBody();
            requestWrapper.setRequestBody(requestBody);
        }
        final Pair pair = metadata.getAnnotation(Pair.class);
        final Object value = metadata.getValue();
        if (value == null) {
            requestBody.addPair(pair.value(), "");
        } else if (value instanceof Map || value.getClass().getAnnotation(PairEntity.class) != null) {
            requestBody.addPairs(value);
        } else {
            requestBody.addPair(pair.value(), value);
        }
    }
}
