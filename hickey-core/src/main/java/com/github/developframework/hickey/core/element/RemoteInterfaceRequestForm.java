package com.github.developframework.hickey.core.element;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * 远程接口请求表单
 * @author qiuzhenhao
 */
@Getter
public class RemoteInterfaceRequestForm {

    private String type;

    private List<RemoteInterfaceRequestFormProperty> properties = new LinkedList<>();

    public RemoteInterfaceRequestForm(String type) {
        this.type = type;
    }

    public void addFormParameter(RemoteInterfaceRequestFormProperty property) {
        properties.add(property);
    }
}
