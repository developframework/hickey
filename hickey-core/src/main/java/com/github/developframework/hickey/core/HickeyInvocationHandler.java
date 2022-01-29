package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.annotations.Endpoint;
import com.github.developframework.hickey.core.annotations.Request;
import com.github.developframework.hickey.core.structs.*;
import com.github.developframework.hickey.core.structs.requestbody.EmptyRequestBody;
import com.github.developframework.hickey.core.utils.U;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author qiushui on 2021-12-30.
 */
public final class HickeyInvocationHandler implements InvocationHandler {

    private final HickeyOptions options;

    private final HickeyComponents components;

    private final Endpoint endpoint;

    private final Predicate<ResponseWrapper> fallPredicate;

    private final Object fallbackInstance;

    public HickeyInvocationHandler(HickeyOptions options, HickeyComponents components, Endpoint endpoint) {
        this.options = options;
        this.components = components;
        this.endpoint = endpoint;
        this.fallPredicate = U.noConstructorNewInstance(endpoint.fallPredicate(), "fallPredicate类必须要有无参构造方法");
        this.fallbackInstance = buildFallbackInstance(endpoint);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        final RequestWrapper requestWrapper = assembleRequestWrapper(method, args);
        components.getInterceptors().forEach(interceptor -> interceptor.intercept(requestWrapper));
        final ResponseWrapper responseWrapper = components.getHickeyHttpExecutor().execute(requestWrapper);
        components.getHttpPostProcessors().forEach(processor -> processor.process(requestWrapper, responseWrapper));
        if (fallPredicate.test(responseWrapper)) {
            return handleReturnValue(method, responseWrapper);
        } else if (fallbackInstance != null) {
            return U.invokeMethod(fallbackInstance, method.getName(), method.getParameterTypes(), args);
        } else {
            throw new HickeyException(method.getName() + "执行失败并且未定义fallback");
        }
    }

    private Object buildFallbackInstance(Endpoint endpoint) {
        final Class<?> fallbackClass = endpoint.fallback();
        Object fallbackInstance = null;
        if (fallbackClass != Void.class) {
            fallbackInstance = U.noConstructorNewInstance(fallbackClass, "fallback类必须要有无参构造方法");
        }
        return fallbackInstance;
    }

    /**
     * 装配请求体封装包
     *
     * @param method 请求方式
     * @param args   接口入参
     * @return RequestInfo
     */
    private RequestWrapper assembleRequestWrapper(Method method, Object[] args) {
        final RequestWrapper requestWrapper = new RequestWrapper();
        final Request request = method.getAnnotation(Request.class);
        requestWrapper.setLabel(request.label());
        requestWrapper.setMethod(request.method());
        requestWrapper.setUrl(endpoint.value() + "/" + request.path());
        requestWrapper.setReadTimeout(
                request.timeout() == -1 ? options.readOption(HickeyOptions.KEY_DEFAULT_READ_TIMEOUT, Duration.class) : Duration.ofSeconds(request.timeout())
        );
        final Charset charset = options.readOption(HickeyOptions.KEY_DEFAULT_CHARSET, Charset.class);
        requestWrapper.setCharset(charset);
        final MimeType mimeType = request.mimeType();
        resolve(method, args, mimeType, requestWrapper);
        final ContentType contentType = new ContentType(mimeType, Map.of("charset", charset.displayName()));
        requestWrapper.getHeaders().put("Content-Type", contentType.toString());
        return requestWrapper;
    }

    /**
     * 分解方法值
     *
     * @param method         请求方式
     * @param args           接口入参
     * @param mimeType       请求体内容格式
     * @param requestWrapper 请求封装包
     */
    private void resolve(Method method, Object[] args, MimeType mimeType, RequestWrapper requestWrapper) {
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final var metadata = new MethodParameterMetadata();
            metadata.setKey(parameter.getName());
            metadata.setAnnotations(parameter.getAnnotations());
            metadata.setValue(args[i]);
            for (MethodParameterResolver resolver : components.getMethodParameterResolvers()) {
                if (resolver.matches(metadata, mimeType)) {
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
