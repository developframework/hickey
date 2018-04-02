package com.github.developframework.hickey.core.exception;

/**
 * 请求失败异常
 * @author qiuzhenhao
 */
public class HickeyRequestFailException extends HickeyException {


    public HickeyRequestFailException(String format, Object... objs) {
        super(format, objs);
    }
}
