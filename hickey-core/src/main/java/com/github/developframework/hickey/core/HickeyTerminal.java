package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.bodyprovider.BodyProvider;
import com.github.developframework.hickey.core.element.*;
import com.github.developframework.hickey.core.exception.HickeyException;
import com.github.developframework.hickey.core.exception.HickeyRequestFailException;
import com.github.developframework.hickey.core.parse.HickeyConfigurationParser;
import com.github.developframework.kite.core.exception.KiteParseXmlException;
import com.github.developframework.toolkit.http.HttpHeader;
import com.github.developframework.toolkit.http.ToolkitHttpClient;
import com.github.developframework.toolkit.http.request.*;
import com.github.developframework.toolkit.http.response.HttpResponse;
import com.github.developframework.toolkit.http.response.HttpResponseBodyProcessor;
import com.github.developframework.toolkit.http.response.SimpleHttpResponseBodyProcessor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    private boolean isStart;

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
        if(isStart) {
            log.warn("Hickey is running.");
            return;
        }
        hickeyConfiguration.initializeKite(configs);
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
     * @param responseBodyProcessorClass 响应体处理类
     * @return 响应体
     */
    public <T extends HttpResponseBodyProcessor<?, ?>> HttpResponse<T> touch(String groupName, String interfaceId, Object data, Class<T> responseBodyProcessorClass){
        if(!isStart) {
            throw new HickeyException("Hickey is not running, please invoke start() first.");
        }
        final RemoteInterfaceGroup remoteInterfaceGroup = hickeyConfiguration.getRemoteInterfaceGroup(groupName);
        final RemoteInterface remoteInterface = remoteInterfaceGroup.extractRemoteInterface(interfaceId);
        RemoteInterfaceRequest interfaceRequest = remoteInterface.getInterfaceRequest();
        HttpRequest httpRequest = initializeHttpRequest(remoteInterfaceGroup, interfaceRequest, data);
        debugShowRequestInfo(interfaceRequest, httpRequest);

        try {
            HttpResponse<T> response = client.request(interfaceRequest.getMethod(), httpRequest, responseBodyProcessorClass);
            return response;
        } catch (Exception e) {
            throw new HickeyRequestFailException(e.getMessage());
        }
    }

    public HttpResponse<SimpleHttpResponseBodyProcessor> touch(String groupName, String interfaceId, Object data){
        return touch(groupName, interfaceId, data, SimpleHttpResponseBodyProcessor.class);
    }

    /**
     * debug显示请求体信息
     *
     * @param interfaceRequest
     * @param httpRequest
     */
    private void debugShowRequestInfo(RemoteInterfaceRequest interfaceRequest, HttpRequest httpRequest) {
        if(log.isInfoEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb
                    .append("【Request】: \n")
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
            if (httpRequest.hasBody()) {
                sb.append("body: \n");
                String body = new String(httpRequest.getBody().serializeBody(httpRequest.getCharset()), httpRequest.getCharset());
                sb.append(body).append('\n');
            }
            log.info(sb.toString());
        }
    }

    /**
     * 初始化请求体
     *
     * @param remoteInterfaceGroup
     * @param interfaceRequest
     * @return
     */
    private HttpRequest initializeHttpRequest(RemoteInterfaceGroup remoteInterfaceGroup, RemoteInterfaceRequest interfaceRequest, final Object data) {
        final String url = interfaceRequest.getUrl().getValue(data);
        final String urlFull = remoteInterfaceGroup.getDomainPrefix() == null ? url : (
                url.startsWith("/") ? (
                      remoteInterfaceGroup.getDomainPrefix() + url) : (remoteInterfaceGroup.getDomainPrefix() + "/" + url
              )
        );
        final HttpRequest httpRequest = new HttpRequest(urlFull);

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
