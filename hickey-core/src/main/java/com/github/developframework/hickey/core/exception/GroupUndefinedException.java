package com.github.developframework.hickey.core.exception;

/**
 * 接口组未定义异常
 * @author qiuzhenhao
 */
public class GroupUndefinedException extends HickeyException {

    public GroupUndefinedException(String groupName) {
        super("The remote interface group \"%s\" is undefined in all hickey configuration source.", groupName);
    }
}
