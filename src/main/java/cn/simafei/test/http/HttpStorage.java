package cn.simafei.test.http;

import cn.simafei.test.config.ApiConfig;
import cn.simafei.test.utils.ReportUtil;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fengpj on 2017/3/18.
 * 缓存设置的http请求参数，服务器保存的参数
 * Cookies， Headers
 */
public class HttpStorage {

    private String rootUrl;
    private boolean useCookie;
    private Map<String, String> paramMap;
    private List<Header> headers;
    private Map<String, String> cookies;

    public HttpStorage() {
        paramMap = new HashMap<>();
        headers = new ArrayList<>();
        cookies = new HashMap<>();
    }

    public HttpStorage(ApiConfig apiConfig) {
        this();
        this.rootUrl = apiConfig.getRootUrl();
        this.useCookie = apiConfig.isUseCookie();
        addParam(apiConfig.getParams());
        for (Map.Entry<String, String> entry : apiConfig.getHeaders().entrySet()) {
            addHeader(entry.getKey(), entry.getValue());
        }
    }

    public void addParam(String key, String value) {
        paramMap.put(key, value);
    }

    public void addParam(Map<String, String> map) {
        paramMap.putAll(map);
    }

    public void addCookie(String name, String value) {
        cookies.put(name, value);
    }

    public void addHeader(Header header) {
        headers.add(header);
    }

    public void addHeader(String name, String value) {
        Header oldHeader = null;
        for (Header header : headers) {
            if (name.equals(header.getName())) {
                oldHeader = header;
                break;
            }
        }
        headers.remove(oldHeader);
        headers.add(new BasicHeader(name, value));
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public boolean isUseCookie() {
        return useCookie;
    }
}
