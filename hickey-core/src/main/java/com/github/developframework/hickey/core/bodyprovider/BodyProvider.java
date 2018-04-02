package com.github.developframework.hickey.core.bodyprovider;

import com.github.developframework.hickey.core.HickeyConfiguration;
import com.github.developframework.hickey.core.element.RemoteInterfaceRequestBody;
import org.dom4j.Element;

/**
 * @author qiuzhenhao
 */
public interface BodyProvider {

    String xmlElementQName();

    void parseHandle(HickeyConfiguration hickeyConfiguration, RemoteInterfaceRequestBody requestBody, Element element);

    String provide(Object data);

}
