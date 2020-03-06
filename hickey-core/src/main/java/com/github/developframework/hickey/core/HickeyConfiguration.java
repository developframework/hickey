package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.element.RemoteInterfaceGroupElement;
import com.github.developframework.hickey.core.exception.GroupUndefinedException;
import com.github.developframework.kite.core.KiteFactory;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 总配置
 * @author qiuzhenhao
 */
public class HickeyConfiguration {

    private Map<String, RemoteInterfaceGroupElement> remoteInterfaceGroupMap = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private KiteFactory kiteFactory;

    /**
     * 添加接口组
     *
     * @param remoteInterfaceGroupName 接口组名称
     * @return 接口组
     */
    public RemoteInterfaceGroupElement addRemoteInterfaceGroup(String remoteInterfaceGroupName) {
        if (remoteInterfaceGroupMap.containsKey(remoteInterfaceGroupName)) {
            return remoteInterfaceGroupMap.get(remoteInterfaceGroupName);
        } else {
            RemoteInterfaceGroupElement remoteInterfaceGroupElement = new RemoteInterfaceGroupElement(remoteInterfaceGroupName);
            remoteInterfaceGroupMap.put(remoteInterfaceGroupName, remoteInterfaceGroupElement);
            return remoteInterfaceGroupElement;
        }
    }

    /**
     * 获得接口组
     *
     * @param remoteInterfaceGroupName
     * @return
     */
    public RemoteInterfaceGroupElement getRemoteInterfaceGroup(String remoteInterfaceGroupName) {
        if (remoteInterfaceGroupMap.containsKey(remoteInterfaceGroupName)) {
            return remoteInterfaceGroupMap.get(remoteInterfaceGroupName);
        } else {
            throw new GroupUndefinedException(remoteInterfaceGroupName);
        }
    }
}
