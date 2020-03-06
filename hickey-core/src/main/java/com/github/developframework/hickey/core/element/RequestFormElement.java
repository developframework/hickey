package com.github.developframework.hickey.core.element;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * 远程接口请求表单
 *
 * @author qiuzhenhao
 */
@Getter
public class RequestFormElement {

    private String type;

    private List<RequestFormPropertyElement> properties = new LinkedList<>();

    public RequestFormElement(String type) {
        this.type = type;
    }

    public void addFormParameter(RequestFormPropertyElement property) {
        properties.add(property);
    }
}
