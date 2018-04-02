package com.github.developframework.hickey.core.bodyprovider;

import com.github.developframework.hickey.core.HickeyConfiguration;
import com.github.developframework.hickey.core.element.RemoteInterfaceRequestBody;
import com.github.developframework.hickey.core.value.HickeyValue;
import org.dom4j.Element;

/**
 * 默认的请求内容提供者
 * <default-provider>
 * @author qiuzhenhao
 */
public class DefaultBodyProvider implements BodyProvider {

    private HickeyValue value;

    @Override
    public String xmlElementQName() {
        return "default-provider";
    }

    @Override
    public void parseHandle(HickeyConfiguration hickeyConfiguration, RemoteInterfaceRequestBody requestBody, Element element) {
        value = new HickeyValue(element.getTextTrim());
    }

    @Override
    public String provide(Object data) {
        return value.getValue(data);
    }
}
