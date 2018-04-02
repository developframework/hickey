package com.github.developframework.hickey.core.element;

import com.github.developframework.hickey.core.value.HickeyValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 远程接口请求头信息
 * @author qiuzhenhao
 */
@AllArgsConstructor
@Getter
public class RemoteInterfaceRequestHeader {

    private String name;

    private HickeyValue value;
}
