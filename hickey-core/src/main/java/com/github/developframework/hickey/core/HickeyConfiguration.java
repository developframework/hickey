package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.element.RemoteInterfaceGroup;
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

    private Map<String, RemoteInterfaceGroup> remoteInterfaceGroupMap = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private KiteFactory kiteFactory;

    /**
     * 添加接口组
     * @param remoteInterfaceGroupName 接口组名称
     * @return 接口组
     */
    public RemoteInterfaceGroup addRemoteInterfaceGroup(String remoteInterfaceGroupName) {
        if(remoteInterfaceGroupMap.containsKey(remoteInterfaceGroupName)) {
            return remoteInterfaceGroupMap.get(remoteInterfaceGroupName);
        } else {
            RemoteInterfaceGroup remoteInterfaceGroup = new RemoteInterfaceGroup(remoteInterfaceGroupName);
            remoteInterfaceGroupMap.put(remoteInterfaceGroupName, remoteInterfaceGroup);
            return remoteInterfaceGroup;
        }
    }

    /**
     * 获得接口组
     * @param remoteInterfaceGroupName
     * @return
     */
    public RemoteInterfaceGroup getRemoteInterfaceGroup(String remoteInterfaceGroupName) {
        if(remoteInterfaceGroupMap.containsKey(remoteInterfaceGroupName)) {
            return remoteInterfaceGroupMap.get(remoteInterfaceGroupName);
        } else {
            throw new GroupUndefinedException(remoteInterfaceGroupName);
        }
    }
}
