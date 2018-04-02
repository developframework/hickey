package com.github.developframework.hickey.core.element;

import com.github.developframework.hickey.core.value.HickeyValue;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * @author qiuzhenhao
 */
@AllArgsConstructor
@Setter
public class RemoteInterfaceRequestFormProperty {

    private String name;

    private HickeyValue value;
}
