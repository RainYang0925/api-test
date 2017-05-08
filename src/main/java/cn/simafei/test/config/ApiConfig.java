package cn.simafei.test.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ApiConfig {

    public ApiConfig(String configFilePath) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(configFilePath);
        Element rootElement = document.getRootElement();

        rootUrl = rootElement.element("rootUrl").getTextTrim();
        useCookie = Boolean.valueOf(rootElement.element("useCookie").getTextTrim());
        Element paramsElement = rootElement.element("params");
        if (paramsElement != null) {
            @SuppressWarnings("unchecked")
            List<Element> paramElements = paramsElement.elements("param");
            paramElements.forEach((ele) -> params.put(ele.attributeValue("name").trim(),
                    ele.attributeValue("value").trim()));
        }
        Element headersElement = rootElement.element("headers");
        if (headersElement != null) {
            @SuppressWarnings("unchecked")
            List<Element> headerElements = rootElement.element("headers").elements("header");
            headerElements.forEach((ele) -> headers.put(ele.attributeValue("name").trim(),
                    ele.attributeValue("value").trim()));
        }
    }

    private String rootUrl;

    private boolean useCookie;

    public boolean isUseCookie() {
        return useCookie;
    }

    private Map<String, String> params = new HashMap<>();

    private Map<String, String> headers = new HashMap<>();

    public String getRootUrl() {
        return rootUrl;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

}
