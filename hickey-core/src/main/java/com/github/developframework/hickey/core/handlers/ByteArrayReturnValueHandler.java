package com.github.developframework.hickey.core.handlers;

import com.github.developframework.hickey.core.HickeyException;
import com.github.developframework.hickey.core.HickeyOptions;
import com.github.developframework.hickey.core.ReturnValueHandler;
import com.github.developframework.hickey.core.structs.ResponseWrapper;
import com.github.developframework.hickey.core.structs.responsebody.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author qiushui on 2022-01-06.
 */
public class ByteArrayReturnValueHandler implements ReturnValueHandler {

    @Override
    public boolean support(Class<?> returnType) {
        return returnType.isArray() && returnType.getComponentType() == byte.class;
    }

    @Override
    public Object handle(Method method, HickeyOptions options, ResponseWrapper responseWrapper) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            final ResponseBody<?> responseBody = responseWrapper.getResponseBody();
            // TODO
            final Object body = responseBody.getBody();
            return os.toByteArray();
        } catch (IOException e) {
            throw new HickeyException(e.getMessage(), e);
        }
    }
}
