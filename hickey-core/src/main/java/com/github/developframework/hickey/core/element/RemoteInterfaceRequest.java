package com.github.developframework.hickey.core.element;

import com.github.developframework.hickey.core.value.HickeyValue;
import com.github.developframework.toolkit.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author qiuzhenhao
 */
@Getter
public class RemoteInterfaceRequest {

    private HickeyValue url;

    private HttpMethod method;

    @Setter
    private String description;

    private List<RemoteInterfaceRequestHeader> headers = new LinkedList<>();

    private List<RemoteInterfaceRequestParameter> parameters = new LinkedList<>();

    @Setter
    private RemoteInterfaceRequestForm form;

    @Setter
    private RemoteInterfaceRequestBody body;

    public RemoteInterfaceRequest(String url, HttpMethod method) {
        this.url = new HickeyValue(url);
        this.method = method;
    }

    public void addHeader(RemoteInterfaceRequestHeader header) {
        headers.add(header);
    }

    public void addParameter(RemoteInterfaceRequestParameter parameter) {
        parameters.add(parameter);
    }

    public boolean hasForm() {
        return form != null;
    }

    public boolean hasBody() {
        return body != null;
    }
}
