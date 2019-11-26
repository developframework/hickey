package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.bodyprovider.BodyProvider;
import com.github.developframework.hickey.core.element.*;
import com.github.developframework.hickey.core.exception.HickeyException;
import com.github.developframework.hickey.core.exception.HickeyRequestFailException;
import com.github.developframework.hickey.core.parse.HickeyConfigurationParser;
import com.github.developframework.kite.core.KiteFactory;
import com.github.developframework.kite.core.exception.KiteParseXmlException;
import develop.toolkit.http.HttpFailedException;
import develop.toolkit.http.JDKToolkitHttpClient;
import develop.toolkit.http.ToolkitHttpClient;
import develop.toolkit.http.request.HttpRequestData;
import develop.toolkit.http.request.body.*;
import develop.toolkit.http.response.DefaultHttpResponseBodyProcessor;
import develop.toolkit.http.response.HttpResponseData;
import develop.toolkit.http.response.HttpResponseDataBodyProcessor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Hickey终端
 *
 */
@Slf4j
@Getter
public class HickeyTerminal {

    private HickeyConfiguration hickeyConfiguration = new HickeyConfiguration();

    private ToolkitHttpClient client = new JDKToolkitHttpClient();

    private Set<HickeyConfigurationSource> sources;

    private boolean isStart;

    private Map<String, HttpResponseDataBodyProcessor> processors = new HashMap<>();

    public HickeyTerminal(String... configs) {
        Objects.requireNonNull(configs);
        this.sources = new HashSet<>();
        for (String config : configs) {
            sources.add(new FileHickeyConfigurationSource(config));
        }
        processors.put("default", new DefaultHttpResponseBodyProcessor());
    }

    public HickeyTerminal(Set<HickeyConfigurationSource> sources) {
        Objects.requireNonNull(sources);
        this.sources = sources;
        processors.put("default", new DefaultHttpResponseBodyProcessor());
    }

    public void addProcessor(String name, HttpResponseDataBodyProcessor processor) {
        processors.put(name, processor);
    }

    /**
     * 开启使用Kite
     * @param configs
     */
    public void useKite(String... configs) {
        if(isStart) {
            log.warn("Hickey is running.");
            return;
        }
        hickeyConfiguration.initializeKite(configs);
    }

    /**
     * 开启使用Kite
     * @param kiteFactory
     */
    public void useKite(KiteFactory kiteFactory) {
        if(isStart) {
            log.warn("Hickey is running.");
            return;
        }
        hickeyConfiguration.setKiteFactory(kiteFactory);
    }


    public synchronized void start() {
        if(isStart) {
            log.warn("Hickey is running.");
            return;
        }
        HickeyConfigurationParser hickeyConfigurationParser = new HickeyConfigurationParser(hickeyConfiguration);
        for (HickeyConfigurationSource source : sources) {
            try {
                hickeyConfigurationParser.parse(source);
                log.info("Hickey loaded source {}.", source.getSourceName());
            } catch (Exception e) {
                e.printStackTrace();
                throw new KiteParseXmlException("Hickey Framework parse configuration source \"%s\" happened error: %s", source.getSourceName(), e.getMessage());
            }
        }
        isStart = true;
    }

    /**
     * 尝试请求
     * @param groupName 接口组名称
     * @param interfaceId 接口id
     * @param data 数据包
     * @return 响应体
     */
    @SuppressWarnings("unchecked")
    public <T, Y> HttpResponseData<T, Y> touch(String groupName, String interfaceId, Object data) {
        if(!isStart) {
            throw new HickeyException("Hickey is not running, please invoke start() first");
        }
        final RemoteInterfaceGroup remoteInterfaceGroup = hickeyConfiguration.getRemoteInterfaceGroup(groupName);
        final RemoteInterface remoteInterface = remoteInterfaceGroup.extractRemoteInterface(interfaceId);
        RemoteInterfaceRequest interfaceRequest = remoteInterface.getInterfaceRequest();
        HttpRequestData httpRequestData = initializeHttpRequest(remoteInterfaceGroup, interfaceRequest, data);
        try {
            HttpResponseData<T, Y> httpResponseData = client.request(httpRequestData, processors.get(remoteInterface.getInterfaceResponse().getProcessorName()));
            debugInfo(httpRequestData, httpResponseData);
            return httpResponseData;
        } catch (HttpFailedException e) {
            throw new HickeyRequestFailException(e.getMessage());
        }
    }

    /**
     * debug显示请求体信息
     *
     * @param httpRequestData
     * @param httpResponseData
     */
    private void debugInfo(HttpRequestData httpRequestData, HttpResponseData httpResponseData) {
        if(log.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb
                .append("\n【Hickey Debug】: \n")
                    .append("url: ").append(httpRequestData.getUrl()).append('\n')
                    .append("method: ").append(httpRequestData.getHttpMethod().name()).append('\n')
                    .append("charset: ").append(httpRequestData.getCharset()).append('\n');
            if (!httpRequestData.getHeaders().isEmpty()) {
                sb.append("header: \n");
                httpRequestData.getHeaders().forEach((k, v) -> sb.append(k).append(": ").append(v).append('\n'));
            }
            if (!httpRequestData.getUrlParameters().isEmpty()) {
                sb.append("parameter: \n");
                httpRequestData.getUrlParameters().forEach((k, v) -> sb.append(k).append(": ").append(v).append('\n'));
            }
            if (httpRequestData.getBody() != null) {
                sb.append("body: \n");
                String body = new String(httpRequestData.getBody().serializeBody(httpRequestData.getCharset()), httpRequestData.getCharset());
                sb.append(body).append('\n');
            }
            log.debug(sb.toString());
        }
    }

    /**
     * 初始化请求体
     *
     * @param remoteInterfaceGroup
     * @param interfaceRequest
     * @return
     */
    private HttpRequestData initializeHttpRequest(RemoteInterfaceGroup remoteInterfaceGroup, RemoteInterfaceRequest interfaceRequest, final Object data) {
        final String url = interfaceRequest.getUrl().getValue(data);
        final String urlFull = remoteInterfaceGroup.getDomainPrefix() == null ? url : (
                url.startsWith("/") ? (remoteInterfaceGroup.getDomainPrefix() + url) : (remoteInterfaceGroup.getDomainPrefix() + "/" + url)
        );
        final HttpRequestData httpRequestData = new HttpRequestData(interfaceRequest.getMethod(), urlFull);

        // 处理parameter
        interfaceRequest.getParameters().forEach(parameter -> {
            final Object value = parameter.getValue().getValue(data);
            httpRequestData.addUrlParameter(parameter.getName(), value);
        });

        // 处理header
        interfaceRequest.getHeaders().forEach(header -> {
            final Object value = header.getValue().getValue(data);
            httpRequestData.addHeader(header.getName(), value.toString());
        });

        //处理form和body
        HttpRequestDataBody httpRequestDataBody = null;
        if (interfaceRequest.hasForm()) {
            RemoteInterfaceRequestForm form = interfaceRequest.getForm();
            switch (form.getType()) {
                case "form-data": {
                    httpRequestDataBody = new FormDataHttpRequestBody("--hickey");
                }
                break;
                case "x-www-form-encoded": {
                    httpRequestDataBody = new FormUrlencodedHttpRequestBody();
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
                    httpRequestDataBody = new JsonRawHttpRequestBody(content);
                }
                break;
                case XML: {
                    httpRequestDataBody = new XmlRawHttpRequestBody(content);
                }
                break;
                case TEXT: {
                    httpRequestDataBody = new TextRawHttpRequestBody(content);
                }
                break;
            }
        }
        httpRequestData.setBody(httpRequestDataBody);
        return httpRequestData;
    }
}
