package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.annotations.Endpoint;

import java.lang.reflect.Proxy;

/**
 * @author qiushui on 2021-12-30.
 */
public final class Hickey {

    private final HickeyOptions options = new HickeyOptions();

    private final HickeyComponents components;

    public Hickey() {
        this(new HickeyConfigure() {
        });
    }

    public Hickey(HickeyConfigure configure) {
        components = new HickeyComponents(options, configure);
    }

    @SuppressWarnings("unchecked")
    public <T> T load(Class<T> interfaceClass) {
        final Endpoint endpoint = interfaceClass.getAnnotation(Endpoint.class);
        if (endpoint == null) {
            throw new HickeyException("接口类未设定注解@Endpoint");
        }
        return (T) Proxy.newProxyInstance(
                HickeyInvocationHandler.class.getClassLoader(),
                new Class[]{interfaceClass},
                new HickeyInvocationHandler(options, components, endpoint)
        );
    }
}
