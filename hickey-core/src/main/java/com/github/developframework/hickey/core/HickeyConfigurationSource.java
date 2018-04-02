package com.github.developframework.hickey.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * 配置源
 * @author qiuzhenhao
 */
public interface HickeyConfigurationSource {

    /**
     * 获得源的输入流
     * @return 输入流
     * @throws IOException IO异常
     */
    InputStream getInputStream() throws IOException;

    /**
     * 获得源名称
     * @return 源名称
     */
    String getSourceName();
}
