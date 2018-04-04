package com.github.developframework.hickey.boot;

import com.github.developframework.hickey.boot.annotation.EnableHickey;
import com.github.developframework.hickey.core.HickeyTerminal;
import com.github.developframework.hickey.spring.HickeyScanLoader;
import com.github.developframework.kite.core.KiteFactory;
import com.github.developframework.toolkit.http.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置Hickey
 * {@link EnableHickey}
 */
@Configuration
@AutoConfigureAfter(name = "com.github.developframework.kite.boot.KiteComponentAutoConfiguration")
@Slf4j
public class HickeyComponentAutoConfiguration {

    @Bean
    @ConditionalOnBean(KiteFactory.class)
    public HickeyTerminal hickeyTerminal(HickeyProperties hickeyProperties, KiteFactory kiteFactory) {
        HickeyTerminal hickeyTerminal = scanHickeyTerminal(hickeyProperties);
        if(hickeyProperties.isUseKite()) {
            hickeyTerminal.useKite(kiteFactory);
        }
        hickeyTerminal.start();
        log.info("Hickey is running.");
        return hickeyTerminal;
    }
    @Bean
    @ConditionalOnMissingBean(KiteFactory.class)
    public HickeyTerminal hickeyTerminal(HickeyProperties hickeyProperties) {
        HickeyTerminal hickeyTerminal = scanHickeyTerminal(hickeyProperties);
        hickeyTerminal.start();
        log.info("Hickey is running.");
        return hickeyTerminal;
    }

    private HickeyTerminal scanHickeyTerminal(HickeyProperties hickeyProperties) {
        final HickeyScanLoader loader = new HickeyScanLoader(hickeyProperties.getLocations());
        HickeyTerminal hickeyTerminal = loader.createHickeyTerminal();
        Option option = hickeyTerminal.getClient().getOption();
        option.setConnectTimeout(hickeyProperties.getConnectTimeout());
        option.setReadTimeout(hickeyProperties.getReadTimeout());
        HickeyProperties.ProxyInfo proxyInfo = hickeyProperties.getProxy();
        if(proxyInfo != null) {
            option.setProxy(proxyInfo.getType(), proxyInfo.getHost(), proxyInfo.getPort());
        }
        return hickeyTerminal;
    }
}
