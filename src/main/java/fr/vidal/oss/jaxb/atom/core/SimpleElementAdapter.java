package fr.vidal.oss.jaxb.atom.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static java.lang.String.format;

public class SimpleElementAdapter extends XmlAdapter<Element, SimpleElement> {

    private DocumentBuilder builder;
    private JAXBContext context;

    private DocumentBuilder builder() throws Exception {
        if (builder == null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            builder = dbf.newDocumentBuilder();
        }
        return builder;
    }

    private JAXBContext context(Class<?> type) throws Exception {
        if (context == null) {
            context = JAXBContext.newInstance(type);
        }
        return context;
    }

    @Override
    public Element marshal(SimpleElement simpleElement) throws Exception {
        if (simpleElement == null) {
            return null;
        }

        Element element = createElement(simpleElement);
        addAttributes(element, simpleElement);
        return element;
    }

    private Element createElement(SimpleElement simpleElement) throws Exception {
        Document document = builder().newDocument();
        context(simpleElement.getClass())
            .createMarshaller()
            .marshal(jaxbElement(simpleElement), document);

        return document.getDocumentElement();
    }

    private JAXBElement<String> jaxbElement(SimpleElement simpleElement) {
        return new JAXBElement<>(
            qualifiedName(simpleElement),
            String.class,
            simpleElement.value()
        );
    }

    private void addAttributes(Element element, SimpleElement simpleElement) {
        for (Attribute attribute : simpleElement.attributes()) {
            addAttribute(element, attribute);
        }
    }

    private void addAttribute(Element element, Attribute attribute) {
        Namespace namespace = attribute.getNamespace();
        String attributeName = attribute.getName();
        String attributeValue = attribute.getValue();

        if (namespace == null) {
            element.setAttribute(attributeName, attributeValue);
            return;
        }

        element.setAttributeNS(
            namespace.uri(),
            format("%s:%s", namespace.prefix(), attributeName),
            attributeValue
        );
    }

    private QName qualifiedName(SimpleElement simpleElement) {
        Namespace namespace = simpleElement.namespace();
        if (namespace != null) {
            return new QName(namespace.uri(), simpleElement.tagName(), namespace.prefix());
        }
        return new QName(simpleElement.tagName());
    }

    @Override
    public SimpleElement unmarshal(Element element) throws Exception {
        return null;
    }
}
