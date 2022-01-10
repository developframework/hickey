package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.structs.ResponseWrapper;

import java.util.function.Predicate;

/**
 * 默认降级判定
 *
 * @author qiushui on 2022-01-10.
 */
public class DefaultFallPredicate implements Predicate<ResponseWrapper> {

    @Override
    public boolean test(ResponseWrapper responseWrapper) {
        final int status = responseWrapper.getStatus();
        return !responseWrapper.executeOK() || (status >= 500 && status < 600);
    }
}
