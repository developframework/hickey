package com.github.developframework.hickey.core.element;

import com.github.developframework.hickey.core.value.HickeyValue;
import develop.toolkit.http.request.HttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 远程接口请求体
 */
@Getter
public class RemoteInterfaceRequest {

     /* 接口Url */
    private HickeyValue url;

     /* 接口请求方式 */
    private HttpMethod method;

    /* 接口描述 */
    @Setter
    private String description;

    /* 接口请求头信息列表 */
    private Map<String, HickeyValue> headers = new LinkedHashMap<>();

    /* 接口url参数列表 */
    private Map<String, HickeyValue> parameters = new LinkedHashMap<>();

    /* 接口请求Body */
    @Setter
    private RemoteInterfaceRequestBody body;

    public RemoteInterfaceRequest(String url, HttpMethod method) {
        this.url = HickeyValue.of(url);
        this.method = method;
    }

    /**
     * 添加header信息
     * @param header header信息
     */
    public void addHeader(String header, HickeyValue hickeyValue) {
        headers.put(header, hickeyValue);
    }

    /**
     * 添加参数
     * @param key 参数
     */
    public void addParameter(String key, HickeyValue hickeyValue) {
        parameters.put(key, hickeyValue);
    }
}
