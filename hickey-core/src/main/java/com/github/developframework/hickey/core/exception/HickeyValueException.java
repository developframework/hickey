package com.github.developframework.hickey.core.exception;

/**
 * @author qiuzhenhao
 */
public class HickeyValueException extends HickeyException {

    public HickeyValueException(String valueName) {
        super("Hickey value \"%s\" is null in data.", valueName);
    }
}
