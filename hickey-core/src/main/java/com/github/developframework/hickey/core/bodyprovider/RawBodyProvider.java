package com.github.developframework.hickey.core.bodyprovider;

/**
 * 请求内容提供者接口
 *
 * @author qiuzhenhao
 */
public interface RawBodyProvider {

    /**
     * 描述如何提供内容
     *
     * @param data
     * @return
     */
    String provide(Object data);
}
