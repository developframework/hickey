package com.github.developframework.hickey.core.exception;

/**
 * 接口定义不唯一异常
 * @author qiuzhenhao
 */
public class InterfaceExistException extends HickeyException {

    public InterfaceExistException(String interfaceId) {
        super("interface id \"%s\" have been defined.", interfaceId);
    }
}
