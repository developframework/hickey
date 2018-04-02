package com.github.developframework.hickey.core.parse;

import com.github.developframework.hickey.core.HickeyConfiguration;
import com.github.developframework.hickey.core.HickeyConfigurationSource;
import com.github.developframework.hickey.core.bodyprovider.BodyProvider;
import com.github.developframework.hickey.core.element.*;
import com.github.developframework.hickey.core.exception.HickeyException;
import com.github.developframework.hickey.core.value.HickeyValue;
import com.github.developframework.toolkit.http.HttpMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.util.Iterator;

/**
 * hickey配置文件解析器
 * dom4j方式
 *
 * @author qiuzhenhao
 */
public class HickeyConfigurationParser {

    private HickeyConfiguration hickeyConfiguration;

    public HickeyConfigurationParser(HickeyConfiguration hickeyConfiguration) {
        this.hickeyConfiguration = hickeyConfiguration;
    }

    public void parse(HickeyConfigurationSource hickeyConfigurationSource) throws IOException, DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(hickeyConfigurationSource.getInputStream());
        Element hickeyConfigurationElement = document.getRootElement();
        parseRemoteInterfaceCollectionElement(hickeyConfigurationElement);
    }

    /**
     * 解析<remote-interfaces>
     *
     * @param hickeyConfigurationElement
     */
    @SuppressWarnings("unchecked")
    private void parseRemoteInterfaceCollectionElement(Element hickeyConfigurationElement) {
        Element remoteInterfacesElement = hickeyConfigurationElement.element("remote-interfaces");
        for (Iterator<Element> remoteInterfacesIterator = remoteInterfacesElement.elementIterator("remote-interface"); remoteInterfacesIterator.hasNext(); ) {
            Element remoteInterfaceElement = remoteInterfacesIterator.next();
            parseRemoteInterfaceElement(remoteInterfaceElement);
        }

    }

    /**
     * 解析<remote-interface>
     *
     * @param remoteInterfaceElement
     */
    private void parseRemoteInterfaceElement(Element remoteInterfaceElement) {
        final String id = remoteInterfaceElement.attributeValue("id");
        RemoteInterface remoteInterface = new RemoteInterface(id);
        hickeyConfiguration.addRemoteInterface(remoteInterface);
        Element requestElement = remoteInterfaceElement.element("request");
        parseRequestElement(remoteInterface, requestElement);
    }

    /**
     * 解析<request>
     *
     * @param remoteInterface
     * @param requestElement
     */
    @SuppressWarnings("unchecked")
    private void parseRequestElement(RemoteInterface remoteInterface, Element requestElement) {
        final String url = requestElement.element("url").getTextTrim();
        final HttpMethod method = HttpMethod.valueOf(requestElement.attributeValue("method"));
        RemoteInterfaceRequest request = new RemoteInterfaceRequest(url, method);

        // 解析 description
        Element descriptionElement = requestElement.element("description");
        if (descriptionElement != null) {
            request.setDescription(descriptionElement.getTextTrim());
        }

        // 解析 headers
        Element headersElement = requestElement.element("headers");
        if (headersElement != null) {
            for (Iterator<Element> headerIterator = headersElement.elementIterator("header"); headerIterator.hasNext(); ) {
                Element headerElement = headerIterator.next();
                final String name = headerElement.attributeValue("name");
                HickeyValue value = new HickeyValue(hickeyConfiguration.getValuePlaceholder(), headerElement.getTextTrim());
                request.addHeader(new RemoteInterfaceRequestHeader(name, value));
            }
        }

        // 解析 parameters
        Element parametersElement = requestElement.element("parameters");
        if (parametersElement != null) {
            for (Iterator<Element> parameterIterator = parametersElement.elementIterator("parameter"); parameterIterator.hasNext(); ) {
                Element parameterElement = parameterIterator.next();
                final String name = parameterElement.attributeValue("name");
                HickeyValue value = new HickeyValue(hickeyConfiguration.getValuePlaceholder(), parameterElement.getTextTrim());
                request.addParameter(new RemoteInterfaceRequestParameter(name, value));
            }
        }

        // 解析 form
        Element formElement = requestElement.element("form");
        if (formElement != null) {
            final String formType = formElement.attributeValue("type");
            RemoteInterfaceRequestForm form = new RemoteInterfaceRequestForm(formType);
            for (Iterator<Element> propertyIterator = parametersElement.elementIterator("property"); propertyIterator.hasNext(); ) {
                Element propertyElement = propertyIterator.next();
                final String name = propertyElement.attributeValue("name");
                HickeyValue value = new HickeyValue(hickeyConfiguration.getValuePlaceholder(), propertyElement.getTextTrim());
                form.addFormParameter(new RemoteInterfaceRequestFormProperty(name, value));
            }
            request.setForm(form);
        }

        // 解析 body
        Element bodyElement = requestElement.element("body");
        if (bodyElement != null) {
            String bodyTypeValue = bodyElement.attributeValue("type");
            RemoteInterfaceRequestBody body = new RemoteInterfaceRequestBody();
            body.setBodyType(RemoteInterfaceRequestBody.BodyType.valueOf(bodyTypeValue));

            // 解析provider
            Element providerElement = bodyElement.element("provider");
            if (providerElement != null && providerElement.hasContent()) {
                Element providerNameElement = (Element) providerElement.elements().get(0);
                final String qName = providerNameElement.getQName().getName();
                BodyProvider bodyProvider = electBodyProvider(qName);
                bodyProvider.parseHandle(hickeyConfiguration, body, providerNameElement);
                body.setBodyProvider(bodyProvider);
            }
            request.setBody(body);
        }
        remoteInterface.setInterfaceRequest(request);
    }

    /**
     * 选举BodyProvider
     *
     * @param providerName
     * @return
     */
    private BodyProvider electBodyProvider(String providerName) {
        for (BodyProvider bodyProvider : hickeyConfiguration.getBodyProviders()) {
            if (bodyProvider.xmlElementQName().equals(providerName)) {
                return bodyProvider;
            }
        }
        throw new HickeyException("Body provider \"%s\" is not exist.", providerName);
    }
}
