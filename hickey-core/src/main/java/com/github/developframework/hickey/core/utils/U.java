package com.github.developframework.hickey.core.utils;

import com.github.developframework.hickey.core.HickeyException;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author qiushui on 2022-01-10.
 */
public abstract class U {

    public static <T> T noConstructorNewInstance(Class<T> clazz, String message) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new HickeyException(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows({NoSuchMethodException.class, InvocationTargetException.class, IllegalAccessException.class})
    public static Object invokeMethod(Class<?> clazz, String name, Class<?>[] parameterTypes, Object[] args) {
        final Method fallbackMethod = clazz.getMethod(name, parameterTypes);
        fallbackMethod.setAccessible(true);
        return fallbackMethod.invoke(clazz, args);
    }
}
