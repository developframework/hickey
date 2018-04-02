package com.github.developframework.hickey.core.exception;

/**
 * 接口未定义异常
 * @author qiuzhenhao
 */
public class InterfaceUndefinedException extends HickeyException {

    public InterfaceUndefinedException(String interfaceId) {
        super("The remote interface id \"%s\" is undefined in all hickey configuration source.", interfaceId);
    }
}
