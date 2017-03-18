package cn.simafei.test.http;

import cn.simafei.test.config.ApiConfig;
import cn.simafei.test.utils.ParamUtil;
import cn.simafei.test.utils.ReportUtil;
import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fengpj on 2017/3/18.
 * 缓存设置的http请求参数，服务器保存的参数
 * Cookies， Headers
 */
public class HTTPCache {

    private String rootUrl;
    private Map<String, String> paramMap;
    private List<Header> headers;
    private List<Cookie> cookies;

    public HTTPCache() {
        paramMap = new HashMap<>();
        headers = new ArrayList<>();
        cookies = new ArrayList<>();
    }

    public HTTPCache(ApiConfig apiConfig) {
        this();
        this.rootUrl = apiConfig.getRootUrl();
        addParam(apiConfig.getParams());
        for (Map.Entry<String, String> entry : apiConfig.getHeaders().entrySet()) {
            addHeader(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 提取json串中的值保存至公共池中
     * @param json    将被提取的json串。
     * @param allSave 所有将被保存的数据：xx=$.jsonpath.xx;oo=$.jsonpath.oo，将$.jsonpath.
     *                xx提取出来的值存放至公共池的xx中，将$.jsonpath.oo提取出来的值存放至公共池的oo中
     */
    public void saveResult(String json, String allSave) {
        if (null == json || "".equals(json) || null == allSave
                || "".equals(allSave)) {
            return;
        }
        allSave = ParamUtil.replaceParam(allSave, paramMap);
        String[] saves = allSave.split(";");
        String key, value;
        for (String save : saves) {
            Pattern pattern = Pattern.compile("([^;=]*)=([^;]*)");
            Matcher m = pattern.matcher(save.trim());
            while (m.find()) {
                key = ParamUtil.replaceResult(json, m.group(1));
                value = ParamUtil.replaceResult(json, m.group(2));
                ReportUtil.log(String.format("存储公共参数   %s值为：%s.", key, value));
                addParam(key, value);
            }
        }
    }

    public void addParam(String key, String value) {
        paramMap.put(key, value);
    }

    public void addParam(Map<String, String> map) {
        paramMap.putAll(map);
    }

    public void addCookie(String name, String value) {
        cookies.add(new BasicClientCookie(name, value));
    }

    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public void addHeader(Header header) {
        headers.add(header);
    }

    public void addHeader(String name, String value) {
        headers.add(new BasicHeader(name, value));
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public String getRootUrl() {
        return rootUrl;
    }
}
