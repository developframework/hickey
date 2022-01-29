package com.github.developframework.hickey.core.structs;

import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;

/**
 * 方法参数元数据
 *
 * @author qiushui on 2021-12-31.
 */
@Getter
@Setter
public final class MethodParameterMetadata {

    private String key;

    private Annotation[] annotations;

    private Object value;

    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        for (Annotation a : annotations) {
            if (annotationClass.isAssignableFrom(a.getClass())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        for (Annotation a : annotations) {
            if (annotationClass.isAssignableFrom(a.getClass())) {
                return (T) a;
            }
        }
        throw new IllegalStateException("未设置@" + annotationClass.getSimpleName());
    }
}
