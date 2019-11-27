package com.github.developframework.hickey.core.bodyprovider;

import com.github.developframework.hickey.core.element.RawType;
import com.github.developframework.hickey.core.exception.HickeyException;
import com.github.developframework.kite.core.KiteFactory;
import com.github.developframework.kite.core.Producer;
import com.github.developframework.kite.core.data.DataModel;
import com.github.developframework.kite.core.data.HashDataModel;
import lombok.AllArgsConstructor;

/**
 * 使用Kite的请求内容提供者
 * @author qiuzhenhao
 */
@AllArgsConstructor
public class KiteRawBodyProvider implements RawBodyProvider {

    private KiteFactory kiteFactory;

    private RawType rawType;

    private String namespace;

    private String templateId;

    private String defaultRoot;

    @Override
    public String provide(Object data) {
        Producer producer = null;
        switch (rawType) {
            case JSON: {
                producer = kiteFactory.getJsonProducer();
            }break;
            case XML: {
                producer = kiteFactory.getXmlProducer();
            }break;
            case PLAIN: {
                throw new HickeyException("Kite provider does not support the \"PLAIN\" body data.");
            }
        }
        DataModel dataModel = HashDataModel.singleton(defaultRoot, data);
        return producer.produce(dataModel, namespace, templateId);
    }
}
