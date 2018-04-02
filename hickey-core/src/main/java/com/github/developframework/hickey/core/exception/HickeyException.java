package com.github.developframework.hickey.core.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Hickey异常
 * @author qiuzhenhao
 */
@Slf4j
public class HickeyException extends RuntimeException {

    public HickeyException(String message) {
        super(message);
        log.error(super.getMessage());
    }

    public HickeyException(String format, Object... objs) {
        super(String.format(format, objs));
        log.error(super.getMessage());
    }
}
