package com.github.developframework.hickey.core;


import com.github.developframework.hickey.core.exception.ConfigurationSourceException;

import java.io.InputStream;

/**
 * 文件配置源
 * @author qiuzhenhao
 */
public class FileHickeyConfigurationSource implements HickeyConfigurationSource {

    private String filename;

    public FileHickeyConfigurationSource(String filename) {
        this.filename = filename;
    }

    @Override
    public InputStream getInputStream() {
        InputStream is = this.getClass().getResourceAsStream(filename);
        if (is == null) {
            throw new ConfigurationSourceException(filename);
        }
        return is;
    }

    @Override
    public String getSourceName() {
        return filename;
    }
}
