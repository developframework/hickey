package com.github.developframework.hickey.core.annotations;

import com.github.developframework.hickey.core.DefaultFallPredicate;
import com.github.developframework.hickey.core.structs.ResponseWrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

/**
 * @author qiushui on 2021-12-30.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Endpoint {

    String value();

    /**
     * 降级处理类
     */
    Class<?> fallback() default Void.class;

    /**
     * 降级判定
     */
    Class<? extends Predicate<ResponseWrapper>> fallPredicate() default DefaultFallPredicate.class;
}
