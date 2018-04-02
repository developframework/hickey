package com.github.developframework.hickey.spring;

import com.github.developframework.hickey.core.HickeyConfigurationSource;
import com.github.developframework.kite.core.ConfigurationSource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * spring resource配置源
 *
 * @author qiuzhenhao
 */
public class SpringResourceHickeyConfigurationSource implements HickeyConfigurationSource {

    /* spring的Resource接口 */
    private Resource resource;

    public SpringResourceHickeyConfigurationSource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return resource.getInputStream();
    }

    @Override
    public String getSourceName() {
        return resource.getFilename();
    }
}
