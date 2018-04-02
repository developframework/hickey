package com.github.developframework.hickey.core.element;

import com.github.developframework.hickey.core.value.HickeyValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author qiuzhenhao
 */
@AllArgsConstructor
@Getter
public class RemoteInterfaceRequestHeader {

    private String name;

    private HickeyValue value;
}
