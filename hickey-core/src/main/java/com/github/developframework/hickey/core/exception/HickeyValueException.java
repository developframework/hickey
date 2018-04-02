package com.github.developframework.hickey.core.exception;

/**
 * 可替换值对象异常
 * @author qiuzhenhao
 */
public class HickeyValueException extends HickeyException {

    public HickeyValueException(String valueName) {
        super("Hickey value \"%s\" is null in data.", valueName);
    }
}
