package com.github.developframework.hickey.core.parse;

import com.github.developframework.hickey.core.HickeyConfiguration;
import com.github.developframework.hickey.core.HickeyConfigurationSource;
import com.github.developframework.hickey.core.bodyprovider.KiteRawBodyProvider;
import com.github.developframework.hickey.core.element.*;
import com.github.developframework.hickey.core.value.HickeyValue;
import develop.toolkit.http.request.HttpMethod;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * hickey配置文件解析器
 * dom4j方式
 *
 */
public class HickeyConfigurationParser {

    private HickeyConfiguration hickeyConfiguration;

    public HickeyConfigurationParser(HickeyConfiguration hickeyConfiguration) {
        this.hickeyConfiguration = hickeyConfiguration;
    }

    public void parse(HickeyConfigurationSource hickeyConfigurationSource) throws IOException, DocumentException {
        Document document = new SAXReader().read(hickeyConfigurationSource.getInputStream());
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
        for(Iterator<Element> iterator = hickeyConfigurationElement.elementIterator("remote-interfaces"); iterator.hasNext();) {
            Element remoteInterfacesElement = iterator.next();
            final String groupName = remoteInterfacesElement.attributeValue("group");
            final RemoteInterfaceGroup group = hickeyConfiguration.addRemoteInterfaceGroup(groupName);
            Element domainPrefixElement = remoteInterfacesElement.element("domain-prefix");
            if (domainPrefixElement != null) {
                group.setDomainPrefix(domainPrefixElement.getTextTrim());
            }
            for (Iterator<Element> remoteInterfacesIterator = remoteInterfacesElement.elementIterator("remote-interface"); remoteInterfacesIterator.hasNext(); ) {
                Element remoteInterfaceElement = remoteInterfacesIterator.next();
                parseRemoteInterfaceElement(group, remoteInterfaceElement);
            }
        }
    }

    /**
     * 解析<remote-interface>
     *
     * @param remoteInterfaceElement
     */
    private void parseRemoteInterfaceElement(RemoteInterfaceGroup group, Element remoteInterfaceElement) {
        final String id = remoteInterfaceElement.attributeValue("id");
        RemoteInterface remoteInterface = new RemoteInterface(group.getGroupName(), id);
        Element requestElement = remoteInterfaceElement.element("request");
        Element responseElement = remoteInterfaceElement.element("response");
        parseRequestElement(remoteInterface, requestElement);
        parseResponseElement(remoteInterface, responseElement);
        group.addRemoteInterface(remoteInterface);
    }

    /**
     * 解析<request>
     *
     * @param remoteInterface
     * @param requestElement
     */
    @SuppressWarnings("unchecked")
    private void parseRequestElement(RemoteInterface remoteInterface, Element requestElement) {
        String url = requestElement.element("url").getTextTrim();
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
                HickeyValue value = HickeyValue.of(headerElement.getTextTrim());
                request.addHeader(name, value);
            }
        }

        // 解析 parameters
        Element parametersElement = requestElement.element("parameters");
        if (parametersElement != null) {
            for (Iterator<Element> parameterIterator = parametersElement.elementIterator("parameter"); parameterIterator.hasNext(); ) {
                Element parameterElement = parameterIterator.next();
                final String key = parameterElement.attributeValue("key");
                String text = parameterElement.getTextTrim();
                HickeyValue value = StringUtils.isEmpty(text) ? HickeyValue.ofValue(key) : HickeyValue.of(text);
                request.addParameter(key, value);
            }
        }

        // 解析 body
        Element bodyElement = requestElement.element("body");
        if (bodyElement != null) {
            RemoteInterfaceRequestBody body = null;
            Element rawElement = bodyElement.element("raw");
            Element formEncodedElement = bodyElement.element("x-www-form-urlencoded");
            Element formDataElement = bodyElement.element("form-data");
            if (rawElement != null) {
                RemoteInterfaceRawRequestBody rawBody = new RemoteInterfaceRawRequestBody();
                rawBody.setType(RawType.valueOf(rawElement.attributeValue("type")));
                Element contentElement = rawElement.element("content");
                Element kiteProviderElement = rawElement.element("kite-provider");
                if (contentElement != null) {
                    rawBody.setContent(HickeyValue.of(contentElement.getTextTrim()));
                } else if (kiteProviderElement != null) {
                    String namespace = kiteProviderElement.attributeValue("namespace");
                    String templateId = kiteProviderElement.attributeValue("template-id");
                    String root = kiteProviderElement.attributeValue("root");
                    rawBody.setRawBodyProvider(new KiteRawBodyProvider(hickeyConfiguration.getKiteFactory(), rawBody.getType(), namespace, templateId, root == null ? "body" : root));
                }
                body = rawBody;
            }

            if (formEncodedElement != null) {
                RemoteInterfaceFormUrlEncodedRequestBody formUrlEncodeBody = new RemoteInterfaceFormUrlEncodedRequestBody();
                Map<String, HickeyValue> parameters = new LinkedHashMap<>();
                for (Iterator<Element> iterator = formEncodedElement.elementIterator("parameter"); iterator.hasNext(); ) {
                    Element parameterElement = iterator.next();
                    String key = parameterElement.attributeValue("key");
                    String value = parameterElement.attributeValue("value");
                    HickeyValue hickeyValue = value == null ? HickeyValue.ofValue(key) : HickeyValue.of(value);
                    parameters.put(key, hickeyValue);
                }
                formUrlEncodeBody.setParameters(parameters);
                body = formUrlEncodeBody;
            }

            if (formDataElement != null) {
                RemoteInterfaceFormDataRequestBody formDataBody = new RemoteInterfaceFormDataRequestBody();
                Map<String, HickeyValue> parameters = new LinkedHashMap<>();
                for (Iterator<Element> iterator = formDataElement.elementIterator("parameter"); iterator.hasNext(); ) {
                    Element parameterElement = iterator.next();
                    String key = parameterElement.attributeValue("key");
                    String text = parameterElement.getTextTrim();
                    HickeyValue value = StringUtils.isEmpty(text) ? HickeyValue.ofValue(key) : HickeyValue.of(text);
                    parameters.put(key, value);
                }
                formDataBody.setParameters(parameters);
                body = formDataBody;
            }
            request.setBody(body);
        }
        remoteInterface.setInterfaceRequest(request);
    }

    private void parseResponseElement(RemoteInterface remoteInterface, Element responseElement) {
        RemoteInterfaceResponse response = new RemoteInterfaceResponse();
        response.setProcessorName(responseElement == null ? "default" : responseElement.attributeValue("processor").trim());
        remoteInterface.setInterfaceResponse(response);
    }
}
