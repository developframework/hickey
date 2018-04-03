package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.bodyprovider.BodyProvider;
import com.github.developframework.hickey.core.bodyprovider.DefaultBodyProvider;
import com.github.developframework.hickey.core.bodyprovider.KiteBodyProvider;
import com.github.developframework.hickey.core.element.RemoteInterfaceGroup;
import com.github.developframework.hickey.core.exception.GroupUndefinedException;
import com.github.developframework.kite.core.KiteFactory;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 总配置
 * @author qiuzhenhao
 */
public class HickeyConfiguration {

    private Map<String, RemoteInterfaceGroup> remoteInterfaceGroupMap = new ConcurrentHashMap<>();

    @Getter
    private List<BodyProvider> bodyProviders = new LinkedList<>();

    @Getter
    private KiteFactory kiteFactory;

    public HickeyConfiguration() {
        registerDefaultBodyProvider();
    }

    /**
     * 注册系统内置BodyProvider
     */
    private void registerDefaultBodyProvider() {
        bodyProviders.add(new DefaultBodyProvider());
    }

    /**
     * 添加BodyProvider
     * @param bodyProvider
     */
    public void addBodyProvider(BodyProvider bodyProvider) {
        bodyProviders.add(bodyProvider);
    }

    /**
     * 初始化Kite
     * @param kiteConfigs 配置文件路径
     */
    public void initializeKite(String... kiteConfigs) {
        kiteFactory = new KiteFactory(kiteConfigs);
        bodyProviders.add(new KiteBodyProvider());
    }

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
