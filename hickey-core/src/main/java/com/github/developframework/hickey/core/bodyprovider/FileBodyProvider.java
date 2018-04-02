package com.github.developframework.hickey.core.bodyprovider;

import com.github.developframework.hickey.core.HickeyConfiguration;
import com.github.developframework.hickey.core.element.RemoteInterfaceRequestBody;
import org.dom4j.Element;

/**
 * @author qiuzhenhao
 */
public class FileBodyProvider implements BodyProvider {

    @Override
    public String xmlElementQName() {
        return "file-provider";
    }

    @Override
    public void parseHandle(HickeyConfiguration hickeyConfiguration, RemoteInterfaceRequestBody requestBody, Element element) {

    }

    @Override
    public String provide(Object data) {
        return null;
    }
}
