package com.github.developframework.hickey.core.exception;

/**
 * 配置源异常
 * @author qiuzhenhao
 */
public class ConfigurationSourceException extends HickeyException {

    public ConfigurationSourceException(String source) {
        super("The hickey configuration source \"\" is not found.", source);
    }
}
