package com.github.developframework.hickey.core.element;

import com.github.developframework.hickey.core.exception.InterfaceExistException;
import com.github.developframework.hickey.core.exception.InterfaceUndefinedException;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qiuzhenhao
 */
public class RemoteInterfaceGroup {

    private Map<String, RemoteInterface> remoteInterfaceMap = new ConcurrentHashMap<>();

    @Getter
    private String groupName;

    @Setter
    @Getter
    private String domainPrefix;

    public RemoteInterfaceGroup(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 增加远程接口配置
     * @param remoteInterface
     */
    public void addRemoteInterface(RemoteInterface remoteInterface) {
        if(remoteInterfaceMap.containsKey(remoteInterface.getId())) {
            throw new InterfaceExistException(remoteInterface.getId());
        } else {
            remoteInterfaceMap.put(remoteInterface.getId(), remoteInterface);
        }
    }

    /**
     * 提取远程接口配置
     * @param interfaceId
     * @return
     */
    public RemoteInterface extractRemoteInterface(String interfaceId) {
        if(remoteInterfaceMap.containsKey(interfaceId)) {
            return remoteInterfaceMap.get(interfaceId);
        } else {
            throw new InterfaceUndefinedException(interfaceId);
        }
    }
}
