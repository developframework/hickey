package com.github.developframework.hickey.core.resolver;

import com.github.developframework.hickey.core.MethodParameterResolver;
import com.github.developframework.hickey.core.annotations.Pair;
import com.github.developframework.hickey.core.structs.MethodParameterMetadata;
import com.github.developframework.hickey.core.structs.MimeType;
import com.github.developframework.hickey.core.structs.RequestWrapper;
import com.github.developframework.hickey.core.structs.requestbody.MultiPartFormDataRequestBody;

import java.io.File;

/**
 * @author qiushui on 2022-01-29.
 */
public final class FormDataPairResolver implements MethodParameterResolver {

    @Override
    public boolean matches(MethodParameterMetadata metadata, MimeType mimeType) {
        return mimeType == MimeType.FORM_DATA;
    }

    @Override
    public void assemble(MethodParameterMetadata metadata, RequestWrapper requestWrapper) {
        MultiPartFormDataRequestBody requestBody = (MultiPartFormDataRequestBody) requestWrapper.getRequestBody();
        if (requestBody == null) {
            requestBody = new MultiPartFormDataRequestBody();
            requestWrapper.setRequestBody(requestBody);
        }
        final Pair pair = metadata.getAnnotation(Pair.class);
        final Object value = metadata.getValue();
        if (value == null) {
            return;
        }
        if (value instanceof File) {
            File file = (File) value;
            requestBody.addPart(pair.value(), MimeType.APPLICATION_OCTET_STREAM.getText(), file);
        } else if (value.getClass().isArray() && value.getClass().getComponentType() == byte.class) {
            byte[] data = (byte[]) value;
            requestBody.addPart(pair.value(), pair.value(), MimeType.APPLICATION_OCTET_STREAM.getText(), data);
        } else {
            requestBody.addPart(pair.value(), value.toString());
        }
    }
}
