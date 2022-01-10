package com.github.developframework.hickey.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.developframework.hickey.core.handlers.ByteArrayReturnValueHandler;
import com.github.developframework.hickey.core.handlers.ResponseWrapperReturnValueHandler;
import com.github.developframework.hickey.core.handlers.StringReturnValueHandler;
import com.github.developframework.hickey.core.postprocessor.PrintLogPostProcessor;
import com.github.developframework.hickey.core.resolver.*;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 组件
 *
 * @author qiushui on 2022-01-07.
 */
@Getter
public final class HickeyComponents {

    private final HickeyHttpExecutor hickeyHttpExecutor;

    private final HickeyOptions options;

    private List<MethodParameterResolver> methodParameterResolvers;

    private List<ReturnValueHandler> returnValueHandlers;

    private List<HickeyInterceptor> interceptors;

    private List<HickeyPostProcessor> httpPostProcessors;

    public HickeyComponents(HickeyOptions options, HickeyConfigure configure) {
        this.options = options;
        hickeyHttpExecutor = configure.httpExecutor(options);
        initializeMethodParameterResolvers(configure);
        initializeReturnValueHandlers(configure);
        initializeInterceptors(configure);
        initializeHttpPostProcessors(configure);
    }

    /**
     * 初始化参数解析器
     */
    private void initializeMethodParameterResolvers(HickeyConfigure configure) {
        final ObjectMapper objectMapper = options.readOption(HickeyOptions.KEY_JACKSON_OBJECTMAPPER, ObjectMapper.class);
        final List<MethodParameterResolver> resolvers = new LinkedList<>(configure.customMethodParameterResolvers());
        // 内置参数解析器
        resolvers.add(new UrlPairResolver());
        resolvers.add(new UrlPairMapResolver());
        resolvers.add(new PathResolver());
        resolvers.add(new HeaderResolver());
        resolvers.add(new FormUrlencodedPairResolver());
        resolvers.add(new FormUrlencodedPairMapResolver());
        resolvers.add(new JsonPairResolver(objectMapper));
        resolvers.add(new JsonPairMapResolver(objectMapper));
        methodParameterResolvers = Collections.unmodifiableList(resolvers);
    }

    /**
     * 初始化返回值处理器
     */
    private void initializeReturnValueHandlers(HickeyConfigure configure) {
        final List<ReturnValueHandler> handlers = new LinkedList<>(configure.customReturnValueHandlers());
        // 内置返回值处理器
        handlers.add(new StringReturnValueHandler());
        handlers.add(new ByteArrayReturnValueHandler());
        handlers.add(new ResponseWrapperReturnValueHandler());
        returnValueHandlers = Collections.unmodifiableList(handlers);
    }

    /**
     * 初始化拦截器
     */
    private void initializeInterceptors(HickeyConfigure configure) {
        final List<HickeyInterceptor> list = new LinkedList<>(configure.customInterceptors());
        interceptors = Collections.unmodifiableList(list);
    }

    /**
     * 初始化后置处理器
     */
    private void initializeHttpPostProcessors(HickeyConfigure configure) {
        final List<HickeyPostProcessor> processors = new LinkedList<>(configure.customHttpPostProcessors());
        // 内置后置处理器
        processors.add(new PrintLogPostProcessor());
        httpPostProcessors = Collections.unmodifiableList(processors);
    }
}
