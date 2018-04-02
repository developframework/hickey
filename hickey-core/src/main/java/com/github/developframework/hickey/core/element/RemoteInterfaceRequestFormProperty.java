package com.github.developframework.hickey.core.element;

import com.github.developframework.hickey.core.value.HickeyValue;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * 远程接口表单属性
 * @author qiuzhenhao
 */
@AllArgsConstructor
@Setter
public class RemoteInterfaceRequestFormProperty {

    private String name;

    private HickeyValue value;
}
