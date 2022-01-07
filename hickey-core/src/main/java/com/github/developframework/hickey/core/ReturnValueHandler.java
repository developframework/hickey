package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.structs.ResponseWrapper;

import java.lang.reflect.Method;

/**
 * 返回值处理器
 *
 * @author qiushui on 2022-01-06.
 */
public interface ReturnValueHandler {

    /**
     * 是否支持返回值类型
     *
     * @param returnType 返回值类型
     */
    boolean support(Class<?> returnType);

    /**
     * 处理逻辑
     *
     * @param method          方法
     * @param options         配置值
     * @param responseWrapper 响应体封装包
     */
    Object handle(Method method, HickeyOptions options, ResponseWrapper responseWrapper);
}
