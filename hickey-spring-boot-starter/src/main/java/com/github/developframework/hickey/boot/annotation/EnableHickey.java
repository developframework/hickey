package com.github.developframework.hickey.boot.annotation;

import com.github.developframework.hickey.boot.HickeyComponentAutoConfiguration;
import com.github.developframework.hickey.boot.HickeyProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableConfigurationProperties(HickeyProperties.class)
@Import(HickeyComponentAutoConfiguration.class)
public @interface EnableHickey {

}
