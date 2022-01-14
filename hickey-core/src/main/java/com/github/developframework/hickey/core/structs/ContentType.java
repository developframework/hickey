package com.github.developframework.hickey.core.structs;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qiushui on 2021-12-31.
 */
@RequiredArgsConstructor
public final class ContentType {

    private final MimeType mimeType;

    private final Map<String, String> parameters;

    public static ContentType matches(String contentTypeValue) {
        final String[] parts = contentTypeValue.split(";");
        for (MimeType mimeType : MimeType.values()) {
            if (mimeType.getText().equals(parts[0])) {
                Map<String, String> map = new HashMap<>();
                for (int i = 1; i < parts.length; i++) {
                    final String[] kv = parts[i].split("=");
                    map.put(kv[0].toLowerCase(), kv[1]);
                }
                return new ContentType(mimeType, map);
            }
        }
        throw new IllegalArgumentException("不支持的Content-Type类型：" + contentTypeValue);
    }

    @Override
    public String toString() {
        return mimeType.getText() + ";" + parameters.entrySet()
                .stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(";"));
    }
}
