package com.github.developframework.hickey.core.bodyprovider;

import com.github.developframework.hickey.core.HickeyConfiguration;
import com.github.developframework.hickey.core.element.RemoteInterfaceRequestBody;
import com.github.developframework.hickey.core.exception.HickeyException;
import com.github.developframework.kite.core.KiteFactory;
import com.github.developframework.kite.core.Producer;
import com.github.developframework.kite.core.data.DataModel;
import com.github.developframework.kite.core.data.HashDataModel;
import org.dom4j.Element;

/**
 * @author qiuzhenhao
 */
public class KiteBodyProvider implements BodyProvider {

    private KiteFactory kiteFactory;

    private RemoteInterfaceRequestBody requestBody;

    private String namespace;

    private String templateId;

    @Override
    public String xmlElementQName() {
        return "kite-provider";
    }

    @Override
    public void parseHandle(HickeyConfiguration hickeyConfiguration, RemoteInterfaceRequestBody requestBody, Element element) {
        this.kiteFactory = hickeyConfiguration.getKiteFactory();
        this.requestBody = requestBody;
        namespace = element.attributeValue("namespace");
        templateId = element.attributeValue("template-id");
    }

    @Override
    public String provide(Object data) {
        Producer producer = null;
        switch(requestBody.getBodyType()) {
            case JSON: {
                producer = kiteFactory.getJsonProducer();
            }break;
            case XML: {
                producer = kiteFactory.getXmlProducer();
            }break;
            case TEXT: {
                throw new HickeyException("Kite provider does not support the \"TEXT\" body data.");
            }
        }
        DataModel dataModel = HashDataModel.singleton("body", data);
        return producer.produce(dataModel, namespace, templateId);
    }
}
