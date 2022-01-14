package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.structs.RequestWrapper;
import com.github.developframework.hickey.core.structs.ResponseWrapper;
import lombok.RequiredArgsConstructor;

/**
 * Http执行器
 *
 * @author qiushui on 2021-12-30.
 */
@RequiredArgsConstructor
public abstract class HickeyHttpExecutor {

    protected final HickeyOptions options;

    protected abstract ResponseWrapper execute(RequestWrapper requestWrapper);

}
