package com.github.developframework.hickey.core.element;

import lombok.Getter;
import lombok.Setter;

/**
 * @author qiuzhenhao
 */
@Getter
public class RemoteInterface {

    private String id;

    @Setter
    private RemoteInterfaceRequest interfaceRequest;
    @Setter
    private InterfaceResponse interfaceResponse;

    public RemoteInterface(String id) {
        this.id = id;
    }
}
