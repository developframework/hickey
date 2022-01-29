package com.github.developframework.hickey.core.annotations;

import com.github.developframework.hickey.core.structs.HttpMethod;
import com.github.developframework.hickey.core.structs.MimeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiushui on 2021-12-30.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Request {

    String label() default "(Undefined)";

    String path() default "";

    HttpMethod method();

    MimeType mimeType() default MimeType.NONE;

    long timeout() default -1;
}
