package com.github.developframework.hickey.core.element;

import develop.toolkit.http.request.body.HttpRequestDataBody;

import java.nio.charset.Charset;

/**
 * 远程接口请求Body
 */
public interface RemoteInterfaceRequestBody {

    HttpRequestDataBody transform(Object data);

    String contentType(Charset charset);
}
