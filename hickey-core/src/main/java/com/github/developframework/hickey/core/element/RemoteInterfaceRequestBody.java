package com.github.developframework.hickey.core.element;

import com.github.developframework.hickey.core.bodyprovider.BodyProvider;
import lombok.Getter;
import lombok.Setter;

/**
 * 远程接口请求Body
 * @author qiuzhenhao
 */
@Getter
@Setter
public class RemoteInterfaceRequestBody {

    public enum BodyType {
        JSON, XML, TEXT
    }

    private BodyType bodyType;

    private BodyProvider bodyProvider;
}
