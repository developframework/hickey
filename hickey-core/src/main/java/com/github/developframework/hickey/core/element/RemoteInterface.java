package com.github.developframework.hickey.core.element;

import lombok.Getter;
import lombok.Setter;

/**
 * 远程接口节点
 * @author qiuzhenhao
 */
@Getter
public class RemoteInterface {

    /* 分组名称 */
    private String groupName;

    /* 接口id */
    private String id;

    @Setter
    private RemoteInterfaceRequest interfaceRequest;

    public RemoteInterface(String groupName, String id) {
        this.groupName = groupName;
        this.id = id;
    }
}
