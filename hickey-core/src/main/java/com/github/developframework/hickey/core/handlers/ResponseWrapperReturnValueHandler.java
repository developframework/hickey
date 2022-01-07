package com.github.developframework.hickey.core.handlers;

import com.github.developframework.hickey.core.HickeyOptions;
import com.github.developframework.hickey.core.ReturnValueHandler;
import com.github.developframework.hickey.core.structs.ResponseWrapper;

import java.lang.reflect.Method;

/**
 * @author qiushui on 2022-01-07.
 */
public class ResponseWrapperReturnValueHandler implements ReturnValueHandler {

    @Override
    public boolean support(Class<?> returnType) {
        return returnType == ResponseWrapper.class;
    }

    @Override
    public Object handle(Method method, HickeyOptions options, ResponseWrapper responseWrapper) {
        return responseWrapper;
    }
}
