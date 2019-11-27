package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.element.RemoteInterface;
import com.github.developframework.hickey.core.element.RemoteInterfaceGroup;
import com.github.developframework.hickey.core.element.RemoteInterfaceRequest;
import com.github.developframework.hickey.core.element.RemoteInterfaceRequestBody;
import com.github.developframework.hickey.core.exception.HickeyException;
import com.github.developframework.hickey.core.exception.HickeyRequestFailException;
import com.github.developframework.hickey.core.parse.HickeyConfigurationParser;
import com.github.developframework.kite.core.KiteFactory;
import com.github.developframework.kite.core.exception.KiteParseXmlException;
import develop.toolkit.base.components.StopWatch;
import develop.toolkit.http.HttpFailedException;
import develop.toolkit.http.JDKToolkitHttpClient;
import develop.toolkit.http.ToolkitHttpClient;
import develop.toolkit.http.request.HttpRequestData;
import develop.toolkit.http.response.ByteHttpResponseBodyProcessor;
import develop.toolkit.http.response.DefaultHttpResponseBodyProcessor;
import develop.toolkit.http.response.HttpResponseData;
import develop.toolkit.http.response.HttpResponseDataBodyProcessor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Hickey终端
 */
@Slf4j
@Getter
public final class HickeyTerminal {

    private HickeyConfiguration hickeyConfiguration = new HickeyConfiguration();

    private ToolkitHttpClient client = new JDKToolkitHttpClient();

    private Set<HickeyConfigurationSource> sources;

    private boolean isStart;

    private final Map<String, HttpResponseDataBodyProcessor> processors = new HashMap<>();

    public HickeyTerminal(String... configs) {
        Objects.requireNonNull(configs);
        this.sources = new HashSet<>();
        for (String config : configs) {
            sources.add(new FileHickeyConfigurationSource(config));
        }
        registerDefaultProcessors();
    }

    public HickeyTerminal(Set<HickeyConfigurationSource> sources) {
        Objects.requireNonNull(sources);
        this.sources = sources;
        registerDefaultProcessors();
    }

    public void addProcessor(String name, HttpResponseDataBodyProcessor processor) {
        processors.put(name, processor);
    }

    /**
     * 注册默认处理器
     */
    private void registerDefaultProcessors() {
        addProcessor("default", new DefaultHttpResponseBodyProcessor());
        addProcessor("byte", new ByteHttpResponseBodyProcessor());
    }

    /**
     * 开启使用Kite
     *
     * @param kiteFactory
     */
    public void useKite(KiteFactory kiteFactory) {
        if (isStart) {
            log.warn("Hickey is running.");
            return;
        }
        hickeyConfiguration.setKiteFactory(kiteFactory);
    }


    public synchronized void start() {
        if (isStart) {
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
     *
     * @param groupName   接口组名称
     * @param interfaceId 接口id
     * @param data        数据包
     * @return 响应体
     */
    @SuppressWarnings("unchecked")
    public <T, Y> HttpResponseData<T, Y> touch(String groupName, String interfaceId, Object data) {
        if (!isStart) {
            throw new HickeyException("Hickey is not running, please invoke start() first");
        }
        final RemoteInterfaceGroup remoteInterfaceGroup = hickeyConfiguration.getRemoteInterfaceGroup(groupName);
        final RemoteInterface remoteInterface = remoteInterfaceGroup.extractRemoteInterface(interfaceId);
        RemoteInterfaceRequest interfaceRequest = remoteInterface.getInterfaceRequest();
        HttpRequestData httpRequestData = initializeHttpRequest(remoteInterfaceGroup, interfaceRequest, data);
        try {
            StopWatch stopWatch = StopWatch.start();
            HttpResponseData<T, Y> httpResponseData = client.request(httpRequestData, processors.get(remoteInterface.getInterfaceResponse().getProcessorName()));
            debugInfo(interfaceRequest, httpRequestData, httpResponseData, stopWatch.formatEnd());
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
    @SuppressWarnings("unchecked")
    private void debugInfo(RemoteInterfaceRequest interfaceRequest, HttpRequestData httpRequestData, HttpResponseData httpResponseData, String costTime) {
        if (log.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb
                    .append("\n============================================================【Hickey Debug】============================================================\n\n")
                    .append("【REQUEST】\n")
                    .append("  description: ").append(interfaceRequest.getDescription()).append('\n')
                    .append("  url: ").append(httpRequestData.getUrl()).append('\n')
                    .append("  method: ").append(httpRequestData.getHttpMethod().name()).append('\n')
                    .append("  parameters: \n");
            if (!httpRequestData.getUrlParameters().isEmpty()) {
                httpRequestData.getUrlParameters().forEach((k, v) -> sb.append("    ").append(k).append(": ").append(v).append('\n'));
            } else {
                sb.append("    (empty)\n");
            }
            sb.append("  headers: \n");
            if (!httpRequestData.getHeaders().isEmpty()) {
                httpRequestData.getHeaders().forEach((k, v) -> sb.append("    ").append(k).append(": ").append(v).append('\n'));
            } else {
                sb.append("    (empty)\n");
            }
            if (httpRequestData.getBody() != null) {
                sb.append("  body: \n");
                String body = new String(httpRequestData.getBody().serializeBody(httpRequestData.getCharset()), httpRequestData.getCharset());
                sb.append("    ").append(body).append('\n');
            }
            sb
                    .append("\n【RESPONSE】\n")
                    .append("  status: ").append(httpResponseData.getHttpStatus()).append('\n')
                    .append("  headers:\n");
            Set<Map.Entry<String, List<String>>> set = httpResponseData.getHeaders().entrySet();
            for (Map.Entry<String, List<String>> entry : set) {
                sb.append("    ").append(entry.getKey()).append(": ").append(StringUtils.join(entry.getValue(), "  |  ")).append('\n');
            }
            sb.append("  body: \n    ");
            if (httpResponseData.isSuccess()) {
                sb.append(httpResponseData.getSuccessBody() != null ? httpResponseData.getSuccessBody() : "(empty)");
            } else {
                sb.append(httpResponseData.getErrorBody() != null ? httpResponseData.getErrorBody() : "(empty)");
            }
            sb.append("\n\n===========================================================【cost time: ").append(costTime).append("】===========================================================\n\n");
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
        interfaceRequest.getParameters().forEach((k, v) -> httpRequestData.addUrlParameter(k, v.getValue(data)));
        // 处理header
        interfaceRequest.getHeaders().forEach((h, v) -> httpRequestData.addHeader(h, v.getValue(data)));
        //处理body
        RemoteInterfaceRequestBody body = interfaceRequest.getBody();
        httpRequestData.setBody(body != null ? body.transform(data) : null);
        return httpRequestData;
    }
}
