package com.github.developframework.hickey.core.element;

import com.github.developframework.hickey.core.exception.InterfaceExistException;
import com.github.developframework.hickey.core.exception.InterfaceUndefinedException;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程接口分组
 *
 * @author qiuzhenhao
 */
public class RemoteInterfaceGroupElement {

    private Map<String, RemoteInterfaceElement> remoteInterfaceMap = new ConcurrentHashMap<>();

    /* 分组名称 */
    @Getter
    private String groupName;

    /* 域名前缀 */
    @Setter
    @Getter
    private String domainPrefix;

    public RemoteInterfaceGroupElement(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 增加远程接口配置
     *
     * @param remoteInterfaceElement
     */
    public void addRemoteInterface(RemoteInterfaceElement remoteInterfaceElement) {
        if (remoteInterfaceMap.containsKey(remoteInterfaceElement.getId())) {
            throw new InterfaceExistException(remoteInterfaceElement.getId());
        } else {
            remoteInterfaceMap.put(remoteInterfaceElement.getId(), remoteInterfaceElement);
        }
    }

    /**
     * 提取远程接口配置
     *
     * @param interfaceId
     * @return
     */
    public RemoteInterfaceElement extractRemoteInterface(String interfaceId) {
        if (remoteInterfaceMap.containsKey(interfaceId)) {
            return remoteInterfaceMap.get(interfaceId);
        } else {
            throw new InterfaceUndefinedException(interfaceId);
        }
    }
}
