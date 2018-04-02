package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.bodyprovider.BodyProvider;
import com.github.developframework.hickey.core.element.RemoteInterface;
import com.github.developframework.hickey.core.element.RemoteInterfaceRequest;
import com.github.developframework.hickey.core.element.RemoteInterfaceRequestBody;
import com.github.developframework.hickey.core.element.RemoteInterfaceRequestForm;
import com.github.developframework.hickey.core.exception.HickeyException;
import com.github.developframework.hickey.core.parse.HickeyConfigurationParser;
import com.github.developframework.kite.core.exception.KiteParseXmlException;
import com.github.developframework.toolkit.http.HttpHeader;
import com.github.developframework.toolkit.http.ToolkitHttpClient;
import com.github.developframework.toolkit.http.request.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Hickey终端
 *
 * @author qiuzhenhao
 */
@Getter
@Slf4j
public class HickeyTerminal {

    private HickeyConfiguration hickeyConfiguration = new HickeyConfiguration();

    private ToolkitHttpClient client = new ToolkitHttpClient();

    private Set<HickeyConfigurationSource> sources;

    public HickeyTerminal(String... configs) {
        Objects.requireNonNull(configs);
        this.sources = new HashSet<>();
        for (String config : configs) {
            sources.add(new FileHickeyConfigurationSource(config));
        }
    }

    public HickeyTerminal(Set<HickeyConfigurationSource> sources) {
        Objects.requireNonNull(sources);
        this.sources = sources;
    }

    /**
     * 开启使用Kite
     * @param configs
     */
    public void useKite(String... configs) {
        hickeyConfiguration.initializeKite(configs);
    }


    public void start() {
        HickeyConfigurationParser hickeyConfigurationParser = new HickeyConfigurationParser(hickeyConfiguration);
        for (HickeyConfigurationSource source : sources) {
            try {
                hickeyConfigurationParser.parse(source);
            } catch (Exception e) {
                e.printStackTrace();
                throw new KiteParseXmlException("Hickey Framework parse configuration source \"%s\" happened error: %s", source.getSourceName(), e.getMessage());
            }
        }
    }

    public void touch(String interfaceId, Object data) {
        final RemoteInterface remoteInterface = hickeyConfiguration.extractRemoteInterface(interfaceId);
        RemoteInterfaceRequest interfaceRequest = remoteInterface.getInterfaceRequest();
        HttpRequest httpRequest = initializeHttpRequest(interfaceRequest, data);
        debugShowRequestInfo(interfaceRequest, httpRequest);
    }

    /**
     * debug显示请求体信息
     *
     * @param interfaceRequest
     * @param httpRequest
     */
    private void debugShowRequestInfo(RemoteInterfaceRequest interfaceRequest, HttpRequest httpRequest) {
        StringBuffer sb = new StringBuffer();
        sb
                .append("【Request】: \n\n")
                .append("url: ").append(httpRequest.getUrl()).append('\n')
                .append("method: ").append(interfaceRequest.getMethod()).append('\n')
                .append("charset: ").append(httpRequest.getCharset()).append('\n')
                .append("header: \n");
        for (HttpHeader httpHeader : httpRequest.getHeaders()) {
            sb.append(httpHeader.getHeaderName()).append(": ").append(httpHeader.getValue()).append('\n');
        }
        sb.append("parameter: \n");
        for (HttpUrlParameter httpUrlParameter : httpRequest.getUrlParameters()) {
            sb.append(httpUrlParameter.getParameterName()).append(": ").append(httpUrlParameter.getValue()).append('\n');
        }
        sb.append("body: \n");
        String body = new String(httpRequest.getBody().serializeBody(httpRequest.getCharset()), httpRequest.getCharset());
        sb.append(body).append('\n');
        System.out.println(sb.toString());
    }

    /**
     * 初始化请求体
     *
     * @param interfaceRequest
     * @return
     */
    private HttpRequest initializeHttpRequest(RemoteInterfaceRequest interfaceRequest, final Object data) {
        final HttpRequest httpRequest = new HttpRequest(interfaceRequest.getUrl());

        // 处理parameter
        interfaceRequest.getParameters().forEach(parameter -> {
            final Object value = parameter.getValue().getValue(data);
            httpRequest.addUrlParameter(parameter.getName(), value);
        });

        // 处理header
        interfaceRequest.getHeaders().forEach(header -> {
            final Object value = header.getValue().getValue(data);
            httpRequest.addHeader(header.getName(), value.toString());
        });

        //处理form和body
        HttpRequestBody httpRequestBody = null;
        if (interfaceRequest.hasForm()) {
            RemoteInterfaceRequestForm form = interfaceRequest.getForm();
            switch (form.getType()) {
                case "form-data": {
                    httpRequestBody = new FormDataHttpRequestBody("--hickey");
                }
                break;
                case "x-www-form-encoded": {
                    httpRequestBody = new FormUrlencodedHttpRequestBody();
                }
                break;
                default: {
                    throw new HickeyException("The form type in ('form-data', 'x-www-form-encoded')");
                }
            }
        } else if (interfaceRequest.hasBody()) {
            RemoteInterfaceRequestBody body = interfaceRequest.getBody();
            BodyProvider bodyProvider = body.getBodyProvider();
            final String content = bodyProvider.provide(data);
            switch (body.getBodyType()) {
                case JSON: {
                    httpRequestBody = new JsonRawHttpRequestBody(content);
                }
                break;
                case XML: {
                    httpRequestBody = new XmlRawHttpRequestBody(content);
                }
                break;
                case TEXT: {
                    httpRequestBody = new TextRawHttpRequestBody(content);
                }
                break;
            }
        }
        httpRequest.setBody(httpRequestBody);
        return httpRequest;
    }
}
