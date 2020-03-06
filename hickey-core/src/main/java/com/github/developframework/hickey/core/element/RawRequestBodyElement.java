package com.github.developframework.hickey.core.element;

import com.github.developframework.hickey.core.bodyprovider.RawBodyProvider;
import com.github.developframework.hickey.core.value.HickeyValue;
import develop.toolkit.http.request.body.HttpRequestDataBody;
import develop.toolkit.http.request.body.JsonRawHttpRequestBody;
import develop.toolkit.http.request.body.PlainRawHttpRequestBody;
import develop.toolkit.http.request.body.XmlRawHttpRequestBody;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.Charset;

@Setter
@Getter
public class RawRequestBodyElement implements RequestBodyElement {

    private RawType type;

    private HickeyValue content;

    private RawBodyProvider rawBodyProvider;

    @Override
    public HttpRequestDataBody transform(Object data) {
        String value;
        if (content != null) {
            value = content.getValue(data);
        } else if (rawBodyProvider != null) {
            value = rawBodyProvider.provide(data);
        } else {
            return null;
        }
        switch (type) {
            case JSON:
                return new JsonRawHttpRequestBody(value);
            case XML:
                return new XmlRawHttpRequestBody(value);
            case PLAIN:
                return new PlainRawHttpRequestBody(value);
            default:
                throw new AssertionError();
        }
    }

    @Override
    public String contentType(Charset charset) {
        switch (type) {
            case JSON:
                return "application/json;charset=" + charset.displayName();
            case XML:
                return "application/xml;charset=" + charset.displayName();
            case PLAIN:
                return "text/plain;charset=" + charset.displayName();
            default:
                throw new AssertionError();
        }
    }
}
