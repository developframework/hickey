package com.github.developframework.hickey.boot;

import com.github.developframework.hickey.boot.annotation.EnableHickey;
import com.github.developframework.hickey.core.HickeyTerminal;
import com.github.developframework.hickey.spring.HickeyScanLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置Hickey
 * {@link EnableHickey}
 */
@Configuration
@Slf4j
public class HickeyComponentAutoConfiguration {

    @Bean
    public HickeyTerminal hickeyTerminal(HickeyProperties hickeyProperties) {
        final HickeyScanLoader loader = new HickeyScanLoader(hickeyProperties.getLocations());
        HickeyTerminal hickeyTerminal = loader.createHickeyTerminal();
        hickeyTerminal.start();
        log.info("Hickey is running.");
        return hickeyTerminal;
    }

}
