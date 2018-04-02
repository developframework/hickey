package com.github.developframework.hickey.spring;

import com.github.developframework.hickey.core.HickeyConfigurationSource;
import com.github.developframework.hickey.core.HickeyTerminal;
import com.github.developframework.hickey.core.exception.HickeyException;
import lombok.Getter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Hickey的扫描加载器
 *
 * @author qiuzhenhao
 */
public class HickeyScanLoader {

    @Getter
    private String locations;

    public HickeyScanLoader(String locations) {
        this.locations = locations;
    }

    /**
     * 创建HickeyTerminal
     *
     * @return HickeyTerminal
     */
    public HickeyTerminal createHickeyTerminal() {
        final String[] locationsArray = StringUtils.tokenizeToStringArray(locations, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            final Set<HickeyConfigurationSource> sources = new HashSet<>();
            for (String locationOne : locationsArray) {
                final Resource[] resources = resolver.getResources(locationOne);
                Arrays.stream(resources).map(SpringResourceHickeyConfigurationSource::new).forEach(sources::add);
            }
            HickeyTerminal hickeyTerminal = new HickeyTerminal(sources);
            hickeyTerminal.start();
            return hickeyTerminal;
        } catch (IOException e) {
            throw new HickeyException("Happen IOException when Spring ResourcePatternResolver get resource: %s", e.getMessage());
        }
    }
}
