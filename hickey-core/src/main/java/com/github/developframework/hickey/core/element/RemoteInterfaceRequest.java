package com.github.developframework.hickey.core.element;

import com.github.developframework.hickey.core.value.HickeyValue;
import com.github.developframework.toolkit.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * 远程接口请求体
 * @author qiuzhenhao
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
    private List<RemoteInterfaceRequestHeader> headers = new LinkedList<>();

    /* 接口url参数列表 */
    private List<RemoteInterfaceRequestParameter> parameters = new LinkedList<>();

    /* 接口请求表单 */
    @Setter
    private RemoteInterfaceRequestForm form;

    /* 接口请求Body */
    @Setter
    private RemoteInterfaceRequestBody body;

    public RemoteInterfaceRequest(String url, HttpMethod method) {
        this.url = new HickeyValue(url);
        this.method = method;
    }

    /**
     * 添加header信息
     * @param header header信息
     */
    public void addHeader(RemoteInterfaceRequestHeader header) {
        headers.add(header);
    }

    /**
     * 添加参数
     * @param parameter 参数
     */
    public void addParameter(RemoteInterfaceRequestParameter parameter) {
        parameters.add(parameter);
    }

    /**
     * 判断是否有表单
     * @return
     */
    public boolean hasForm() {
        return form != null;
    }

    /**
     * 判断是否有Body
     * @return
     */
    public boolean hasBody() {
        return body != null;
    }
}
