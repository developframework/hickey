package com.github.developframework.hickey.core.element;

import com.github.developframework.hickey.core.value.HickeyValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 远程接口url参数
 * @author qiuzhenhao
 */
@AllArgsConstructor
@Getter
public class RemoteInterfaceRequestParameter {

    private String name;

    private HickeyValue value;
}
