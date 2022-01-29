package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.structs.MethodParameterMetadata;
import com.github.developframework.hickey.core.structs.MimeType;
import com.github.developframework.hickey.core.structs.RequestWrapper;

/**
 * 方法参数解析器
 *
 * @author qiushui on 2022-01-03.
 */
public interface MethodParameterResolver {

    /**
     * 判定是否需要该解析器处理
     *
     * @param metadata 方法参数元数据
     * @param mimeType 请求体内容格式
     * @return 是否
     */
    boolean matches(MethodParameterMetadata metadata, MimeType mimeType);

    /**
     * 装配
     *
     * @param metadata       方法参数元数据
     * @param requestWrapper 请求信息
     */
    void assemble(MethodParameterMetadata metadata, RequestWrapper requestWrapper);
}
