package com.github.developframework.hickey.core;

import java.util.Collections;
import java.util.List;

/**
 * @author qiushui on 2022-01-03.
 */
public interface HickeyConfigure {

    default HickeyHttpExecutor httpExecutor(HickeyOptions options) {
        return new JDKHttpClientExecutor(options);
    }

    /**
     * 添加自定义方法参数解析器
     */
    default List<MethodParameterResolver> customMethodParameterResolvers() {
        return Collections.emptyList();
    }

    /**
     * 添加自定义返回值处理器
     */
    default List<ReturnValueHandler> customReturnValueHandlers() {
        return Collections.emptyList();
    }

    /**
     * 添加自定义拦截器
     */
    default List<HickeyInterceptor> customInterceptors() {
        return Collections.emptyList();
    }

    /**
     * 添加自定义后置处理器
     */
    default List<HickeyPostProcessor> customHttpPostProcessors() {
        return Collections.emptyList();
    }
}
