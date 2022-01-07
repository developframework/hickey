package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.annotations.Endpoint;
import com.github.developframework.hickey.core.annotations.Request;
import com.github.developframework.hickey.core.structs.*;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.time.Duration;

/**
 * @author qiushui on 2021-12-30.
 */
@RequiredArgsConstructor
public final class HickeyInvocationHandler implements InvocationHandler {

    private final HickeyOptions options;

    private final HickeyComponents components;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        final RequestWrapper requestWrapper = assembleRequestWrapper(method, args);
        components.getInterceptors().forEach(interceptor -> interceptor.intercept(requestWrapper));
        final ResponseWrapper responseWrapper = components.getHickeyHttpExecutor().execute(requestWrapper);
        components.getHttpPostProcessors().forEach(processor -> processor.process(requestWrapper, responseWrapper));
        return handleReturnValue(method, responseWrapper);
    }

    /**
     * 装配请求体封装包
     *
     * @param method 请求方式
     * @param args   接口入参
     * @return RequestInfo
     */
    private RequestWrapper assembleRequestWrapper(Method method, Object[] args) {
        final Endpoint endpoint = method.getDeclaringClass().getAnnotation(Endpoint.class);
        if (endpoint == null) {
            throw new IllegalStateException("接口类未设定注解@Endpoint");
        }
        final RequestWrapper requestWrapper = new RequestWrapper();
        final Request request = method.getAnnotation(Request.class);
        requestWrapper.setLabel(request.label());
        requestWrapper.setMethod(request.method());
        requestWrapper.setUrl(endpoint.value() + "/" + request.path());
        requestWrapper.setReadTimeout(
                request.timeout() == -1 ? options.readOption(HickeyOptions.KEY_DEFAULT_READ_TIMEOUT, Duration.class) : Duration.ofSeconds(request.timeout())
        );
        requestWrapper.setCharset(
                options.readOption(HickeyOptions.KEY_DEFAULT_CHARSET, Charset.class)
        );
        resolve(method, args, request.contentType(), requestWrapper);
        requestWrapper.getHeaders().put("Content-Type", request.contentType().getText());
        return requestWrapper;
    }

    /**
     * 分解方法值
     *
     * @param method         请求方式
     * @param args           接口入参
     * @param contentType    请求体内容格式
     * @param requestWrapper 请求封装包
     */
    private void resolve(Method method, Object[] args, ContentType contentType, RequestWrapper requestWrapper) {
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final var metadata = new MethodParameterMetadata();
            metadata.setKey(parameter.getName());
            metadata.setAnnotations(parameter.getAnnotations());
            metadata.setValue(args[i]);
            for (MethodParameterResolver resolver : components.getMethodParameterResolvers()) {
                if (resolver.matches(metadata, contentType)) {
                    resolver.assemble(metadata, requestWrapper);
                    break;
                }
            }
        }
        if (requestWrapper.getRequestBody() == null) {
            requestWrapper.setRequestBody(new EmptyRequestBody());
        }
    }

    /**
     * 处理返回值
     *
     * @param method          方法
     * @param responseWrapper 响应体封装包
     */
    private Object handleReturnValue(Method method, ResponseWrapper responseWrapper) {
        final Class<?> returnType = method.getReturnType();
        for (ReturnValueHandler handler : components.getReturnValueHandlers()) {
            if (handler.support(returnType)) {
                return handler.handle(method, options, responseWrapper);
            }
        }
        return null;
    }
}