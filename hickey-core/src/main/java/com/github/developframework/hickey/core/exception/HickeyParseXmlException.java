package com.github.developframework.hickey.core.exception;

/**
 * 解析XML异常
 * @author qiuzhenhao
 */
public class HickeyParseXmlException extends HickeyException {

    public HickeyParseXmlException(String format, Object... objs) {
        super(format, objs);
    }
}
