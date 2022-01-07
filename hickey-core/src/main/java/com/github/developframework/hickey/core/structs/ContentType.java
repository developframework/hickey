package com.github.developframework.hickey.core.structs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author qiushui on 2021-12-31.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ContentType {

    NONE(null),

    APPLICATION_JSON("application/json"),

    APPLICATION_XML("application/xml"),

    FORM_URLENCODED("application/x-www-form-urlencoded"),

    FORM_DATA("multipart/form-data"),

    TEXT_PLAIN("text/plain"),

    TEXT_JSON("text/json"),

    TEXT_XML("text/xml"),

    TEXT_HTML("text/html");

    @Getter
    private final String text;

    public static ContentType matches(String contentTypeValue) {
        for (ContentType contentType : ContentType.values()) {
            if (contentTypeValue.startsWith(contentType.getText())) {
                return contentType;
            }
        }
        throw new IllegalArgumentException("不支持的Content-Type类型");
    }
}
