package com.github.developframework.hickey.core.bodyprovider;

import com.github.developframework.hickey.core.HickeyConfiguration;
import com.github.developframework.hickey.core.element.RemoteInterfaceRequestBody;
import org.dom4j.Element;

/**
 * 请求内容提供者接口
 * @author qiuzhenhao
 */
public interface BodyProvider {

    /**
     * 配置文件xml节点的名称
     * @return
     */
    String xmlElementQName();

    /**
     * 定义配置文件解析过程
     * @param hickeyConfiguration
     * @param requestBody
     * @param element
     */
    void parseHandle(HickeyConfiguration hickeyConfiguration, RemoteInterfaceRequestBody requestBody, Element element);

    /**
     * 描述如何提供内容
     * @param data
     * @return
     */
    String provide(Object data);

}
