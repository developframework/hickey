package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.structs.RequestWrapper;

/**
 * 拦截器 在发起Http请求之前的处理扩展
 *
 * @author qiushui on 2022-01-07.
 */
public interface HickeyInterceptor {

    void intercept(RequestWrapper requestWrapper);
}
