package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.bodyprovider.BodyProvider;
import com.github.developframework.hickey.core.bodyprovider.DefaultBodyProvider;
import com.github.developframework.hickey.core.bodyprovider.KiteBodyProvider;
import com.github.developframework.hickey.core.element.RemoteInterface;
import com.github.developframework.hickey.core.exception.InterfaceExistException;
import com.github.developframework.hickey.core.exception.InterfaceUndefinedException;
import com.github.developframework.hickey.core.value.ValuePlaceholder;
import com.github.developframework.kite.core.KiteFactory;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qiuzhenhao
 */
public class HickeyConfiguration {

    private Map<String, RemoteInterface> remoteInterfaceMap = new ConcurrentHashMap<>();

    @Getter
    private ValuePlaceholder valuePlaceholder = new ValuePlaceholder();

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
     * 初始化Kite
     * @param kiteConfigs
     */
    public void initializeKite(String... kiteConfigs) {
        kiteFactory = new KiteFactory(kiteConfigs);
        bodyProviders.add(new KiteBodyProvider());
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
