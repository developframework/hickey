package com.github.developframework.hickey.core.structs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author qiushui on 2022-01-12.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MimeType {

    NONE(null, false),

    APPLICATION_JSON("application/json", true),

    APPLICATION_XML("application/xml", true),

    FORM_URLENCODED("application/x-www-form-urlencoded", false),

    FORM_DATA("multipart/form-data", false),

    TEXT_PLAIN("text/plain", true),

    TEXT_JSON("text/json", true),

    TEXT_XML("text/xml", true),

    TEXT_HTML("text/html", true),

    APPLICATION_OCTET_STREAM("application/octet-stream", false);

    @Getter
    private final String text;

    private final boolean readable;
}
