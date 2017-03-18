package cn.simafei.test.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.simafei.test.beans.ApiDataBean;
import cn.simafei.test.exceptions.ErrorRespStatusException;
import cn.simafei.test.utils.*;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;

public class SSLClient extends DefaultHttpClient {
    private HTTPCache cache;

    public SSLClient(HTTPCache cache) throws Exception {
        this.cache = cache;
        SSLContext ctx = SSLContext.getInstance("TLS");
        X509TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        ctx.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory ssf = new SSLSocketFactory(ctx,
                SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = this.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, ssf));
    }

    /**
     * 格式化url,替换路径参数等。
     */
    public String getRequestUrl(String root, String path) {
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

    /**
     * 封装请求方法
     * TODO 添加Cookie
     * @return 请求方法
     */
    public HttpUriRequest createHttpRequest(ApiDataBean apiDataBean) throws UnsupportedEncodingException {
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
        String responseData;
        try {
            // 执行
            HttpResponse response = execute(method);
            int responseStatus = response.getStatusLine().getStatusCode();
            if (StringUtil.isNotEmpty(apiDataBean.getStatus())) {
                Assert.assertEquals(responseStatus + "", apiDataBean.getStatus(), "返回状态码与预期不符合!");
            } else {
                // 非2开头状态码为异常请求，抛异常后会进行重跑
                if (200 > responseStatus || responseStatus >= 300) {
                    throw new ErrorRespStatusException("返回状态码异常：" + responseStatus);
                }
            }
            responseData = getResult(response);
        } finally {
            method.abort();
        }
        // 输出返回数据log
        ReportUtil.log("resp:" + responseData);

        // 对返回结果进行提取保存。
        cache.saveResult(responseData, apiDataBean.getSave());

        return responseData;
    }

    private String getResult(HttpResponse response) throws IOException {
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
            return "{\"filePath\":\"" + filePath + "\"}";
        } else {
            return EntityUtils.toString(respEntity);
        }
    }
}
