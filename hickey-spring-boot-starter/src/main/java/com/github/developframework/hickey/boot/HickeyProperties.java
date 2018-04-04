package com.github.developframework.hickey.boot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.Proxy;

@Component
@ConfigurationProperties(prefix = "hickey")
@Getter
@Setter
public class HickeyProperties {

    private String locations = "classpath*:hickey/*.xml";

    private boolean useKite = true;

    private int connectTimeout = 5000;

    private int readTimeout = 5000;

    private ProxyInfo proxy;

    @Getter
    @Setter
    public static class ProxyInfo {

        private Proxy.Type type;

        private String host;

        private int port;
    }
}
