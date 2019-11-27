package com.github.developframework.hickey.core.element;

import com.github.developframework.hickey.core.value.HickeyValue;
import develop.toolkit.http.request.body.FormUrlencodedHttpRequestBody;
import develop.toolkit.http.request.body.HttpRequestDataBody;
import lombok.Setter;

import java.nio.charset.Charset;
import java.util.Map;

@Setter
public class RemoteInterfaceFormUrlEncodedRequestBody implements RemoteInterfaceRequestBody {

    private Map<String, HickeyValue> parameters;

    @Override
    public HttpRequestDataBody transform(Object data) {
        FormUrlencodedHttpRequestBody body = new FormUrlencodedHttpRequestBody();
        parameters.forEach((k, v) -> body.addParameter(k, v.getValue(data)));
        return body;
    }

    @Override
    public String contentType(Charset charset) {
        return "application/x-www-form-urlencoded;charset=" + charset.displayName();
    }
}
