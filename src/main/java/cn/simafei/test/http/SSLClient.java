package cn.simafei.test.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import cn.simafei.test.beans.ApiDataBean;
import cn.simafei.test.exceptions.ErrorRespStatusException;
import cn.simafei.test.utils.*;
import org.apache.commons.codec.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;

public class SSLClient {
    private HTTPCache cache;
    private PoolingHttpClientConnectionManager poolConnManager;
    private CookieStore cookieStore = new BasicCookieStore();

    public SSLClient(HTTPCache cache) throws Exception {
        this.cache = cache;

        SSLContext ctx = SSLContexts.custom().loadTrustMaterial(null, (chain, authType) -> true).build();
        SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", ssf).build();
        // 初始化连接管理器
        poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolConnManager.setMaxTotal(Constants.CLIENT_POOL_MAX_SIZE);
        poolConnManager.setDefaultMaxPerRoute(Constants.MAX_PER_ROUTE);
        poolConnManager.setDefaultConnectionConfig(
                ConnectionConfig.custom().setCharset(Charsets.toCharset(Constants.DEFAULT_ENCODE)).build());
        poolConnManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(Constants.SOCKET_TIMEOUT)
                .setTcpNoDelay(Constants.IS_TCP_NO_DELAY).build());
    }


    /**
     * 格式化url,替换路径参数等。
     */
    private String getRequestUrl(String root, String path) {
        // 替换url中的参数
        path = ParamUtil.replaceFunc(path, cache.getParamMap());
        if (path.startsWith("http")) {
            return path;
        }
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        if (root.endsWith("/")) {
            return root + path;
        } else {
            return root + "/" + path;
        }
    }

    private CloseableHttpClient getHttpsClient() {
        return HttpClients.custom().setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(Constants.CONNECT_TIMEOUT)
                        .setSocketTimeout(Constants.SOCKET_TIMEOUT).build())
                .setDefaultCookieStore(cookieStore)
                .build();
    }
    /**
     * 封装请求方法
     * @return 请求方法
     */
    private HttpUriRequest createHttpRequest(ApiDataBean apiDataBean) throws UnsupportedEncodingException {
        // 处理url
        String url = getRequestUrl(cache.getRootUrl(), apiDataBean.getUrl());
        String body = apiDataBean.getBody();
        String param = apiDataBean.getParam();
        String method = apiDataBean.getMethod();
        ReportUtil.log("method:" + method);
        ReportUtil.log("url:" + url);
        if (apiDataBean.isPostJson()) {
            ReportUtil.log("body:" + body.replace("\r\n", "").replace("\n", ""));
        } else {
            ReportUtil.log("param:" + param);
        }
        List<Header> headers = cache.getHeaders();

        if ("post".equalsIgnoreCase(method)) {
            // 封装post方法
            HttpPost postMethod = new HttpPost(url);
            postMethod.setHeaders(headers.toArray(new Header[headers.size()]));

            if (apiDataBean.isPostJson()) {
                HttpEntity entity = new StringEntity(ParamUtil.replaceFunc(body, cache.getParamMap()), StandardCharsets.UTF_8);
                postMethod.setEntity(entity);
            } else if (param != null && !"".equals(param)) {
                List<NameValuePair> params = new ArrayList<>();
                for (String pair : param.split("&")) {
                    String[] nameValue = pair.split("=");
                    params.add(new BasicNameValuePair(nameValue[0], ParamUtil.replaceFunc(nameValue[1], cache.getParamMap())));
                }
                postMethod.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            }
            return postMethod;
        } else {
            // 封装get方法
            HttpGet getMethod = new HttpGet(url + "?" + param);
            getMethod.setHeaders(headers.toArray(new Header[headers.size()]));
            return getMethod;
        }
    }

    public String sendRequest(ApiDataBean apiDataBean) throws IOException, ErrorRespStatusException {
        // 封装请求方法
        HttpUriRequest method = createHttpRequest(apiDataBean);
        String result;
        try {
            HttpResponse response = getHttpsClient().execute(method);
            int responseStatus = response.getStatusLine().getStatusCode();
            if (StringUtil.isNotEmpty(apiDataBean.getStatus())) {
                Assert.assertEquals(responseStatus + "", apiDataBean.getStatus(), "返回状态码与预期不符合!");
            } else {
                // 非2开头状态码为异常请求，抛异常后会进行重跑
                if (200 > responseStatus || responseStatus >= 300) {
                    throw new ErrorRespStatusException("返回状态码异常：" + responseStatus);
                }
            }

            if (cache.isUseCookie()) {
                StringBuilder sb = new StringBuilder();
                int i = 0;
                for (Cookie cookie : cookieStore.getCookies()) {
                    cache.addCookie(cookie.getName(), cookie.getValue());
                }
                for (Map.Entry<String, String> entry : cache.getCookies().entrySet()) {
                    if (i++ > 0) {
                        sb.append("; ");
                    }
                    sb.append(entry.getKey()).append("=").append(entry.getValue());
                }
                cache.addHeader("Cookie", sb.toString());
            }
            result = getResult(response);
        } finally {
            method.abort();
        }
        // 输出返回数据log
        ReportUtil.log("resp:" + result);

        // 对返回结果进行提取保存。
        cache.saveResult(result, apiDataBean.getSave());

        return result;
    }

    private String getResult(HttpResponse response) throws IOException {
        String result;
        HttpEntity respEntity = response.getEntity();
        Header respContentType = response.getFirstHeader("Content-Type");
        if (respContentType != null
                && respContentType.getValue().contains("download")) {
            String conDisposition = response.getFirstHeader("Content-disposition").getValue();
            String fileType = conDisposition.substring(
                    conDisposition.lastIndexOf("."),
                    conDisposition.length());
            String filePath = "download/" + RandomUtil.getRandom(8, false)
                    + fileType;
            InputStream is = response.getEntity().getContent();
            Assert.assertTrue(FileUtil.writeFile(is, filePath), "下载文件失败。");
            // 将下载文件的路径放到{"filePath":"path"}进行返回
            result = "{\"filePath\":\"" + filePath + "\"}";
        } else {
            result =  EntityUtils.toString(respEntity);
        }
        EntityUtils.consume(respEntity);
        return result;
    }
}
