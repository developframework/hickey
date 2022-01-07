package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.structs.RequestWrapper;
import com.github.developframework.hickey.core.structs.ResponseWrapper;

/**
 * Http后置处理
 *
 * @author qiushui on 2022-01-06.
 */
public interface HickeyPostProcessor {

    void process(RequestWrapper requestWrapper, ResponseWrapper responseWrapper);
}
