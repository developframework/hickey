package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.element.RemoteInterfaceElement;
import com.github.developframework.hickey.core.element.RemoteInterfaceGroupElement;
import com.github.developframework.hickey.core.element.RequestElement;
import com.github.developframework.hickey.core.exception.HickeyException;
import com.github.developframework.hickey.core.exception.HickeyRequestFailException;
import com.github.developframework.hickey.core.parse.HickeyConfigurationParser;
import com.github.developframework.kite.core.KiteFactory;
import com.github.developframework.kite.core.exception.KiteParseXmlException;
import develop.toolkit.base.utils.DateTimeAdvice;
import develop.toolkit.base.utils.K;
import develop.toolkit.http.HttpFailedException;
import develop.toolkit.http.JDKToolkitHttpClient;
import develop.toolkit.http.ToolkitHttpClient;
import develop.toolkit.http.request.HttpRequestData;
import develop.toolkit.http.response.HttpResponseData;
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
     *
     * @param kiteFactory
     */
    public void useKite(KiteFactory kiteFactory) {
        if (isStart) {
            log.warn("HickeyTerminal is running.");
            return;
        }
        hickeyConfiguration.setKiteFactory(kiteFactory);
    }


    public synchronized void start() {
        if (isStart) {
            log.warn("HickeyTerminal is running.");
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
    public HttpResponseData touch(String groupName, String interfaceId, Object data) throws HickeyRequestFailException {
        if (!isStart) {
            throw new HickeyException("HickeyTerminal is not running, please invoke start() first");
        }
        final RemoteInterfaceGroupElement groupElement = hickeyConfiguration.getRemoteInterfaceGroup(groupName);
        final RemoteInterfaceElement interfaceElement = groupElement.extractRemoteInterface(interfaceId);
        final RequestElement requestElement = interfaceElement.getInterfaceRequest();
        final HttpRequestData requestData = initializeHttpRequestData(groupElement.getDomainPrefix(), requestElement, data);
        try {
            final HttpResponseData responseData = client.request(requestData);
            debugInfo(requestElement, requestData, responseData);
            return responseData;
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
    private void debugInfo(RequestElement interfaceRequest, HttpRequestData httpRequestData, HttpResponseData httpResponseData) {
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
                sb.append("    ").append(httpRequestData.stringBody()).append('\n');
            }
            sb
                    .append("\n【RESPONSE】\n")
                    .append("  status: ").append(httpResponseData.getHttpStatus()).append('\n')
                    .append("  headers:\n");
            for (Map.Entry<String, List<String>> entry : httpResponseData.getHeaders().entrySet()) {
                sb.append("    ").append(entry.getKey()).append(": ").append(StringUtils.join(entry.getValue(), "  |  ")).append('\n');
            }
            sb.append("  body: \n    ").append(httpResponseData.stringBody());
            sb.append("\n\n===========================================================【cost time: ").append(DateTimeAdvice.millisecondPretty(httpResponseData.getCostTime())).append("】===========================================================\n\n");
            log.debug(sb.toString());
        }
    }

    /**
     * 初始化请求体
     *
     * @param domainPrefix
     * @param requestElement
     * @return
     */
    private HttpRequestData initializeHttpRequestData(String domainPrefix, RequestElement requestElement, final Object data) {
        final String url = requestElement.getUrl().getValue(data);
        final String urlFull = domainPrefix == null ? url : (
                url.startsWith("/") ? (domainPrefix + url) : (domainPrefix + "/" + url)
        );
        final HttpRequestData httpRequestData = new HttpRequestData(requestElement.getMethod(), urlFull)
                .addBody(
                        K.map(requestElement.getBody(), body -> body.transform(data))
                );
        // 处理parameter
        requestElement.getParameters().forEach((k, v) -> httpRequestData.addUrlParameter(k, v.getValue(data)));
        // 处理header
        requestElement.getHeaders().forEach((h, v) -> httpRequestData.addHeader(h, v.getValue(data)));
        return httpRequestData;
    }
}
