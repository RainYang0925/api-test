package cn.simafei.test;

import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.simafei.test.beans.BaseBean;
import cn.simafei.test.http.HTTPCache;
import cn.simafei.test.http.SSLClient;
import cn.simafei.test.utils.*;
import org.dom4j.DocumentException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import cn.simafei.test.beans.ApiDataBean;
import cn.simafei.test.config.ApiConfig;
import cn.simafei.test.listeners.AutoTestListener;
import cn.simafei.test.listeners.RetryListener;

@Listeners({AutoTestListener.class, RetryListener.class})
public class ApiTest {

    private static HTTPCache cache;

    /**
     * 所有api测试用例数据
     */
    private static List<ApiDataBean> dataList = new ArrayList<>();

    private static SSLClient client;

    /**
     * 初始化测试数据
     */
    @Parameters("envName")
    @BeforeSuite
    public void init(@Optional("api-config-online") String envName) throws Exception {
        // api-config-online-crm
        String configFilePath = Paths.get(System.getProperty("user.dir"), "conf", "api", envName + ".xml").toString();
        ReportUtil.log("api config path:" + configFilePath);
        ApiConfig apiConfig = new ApiConfig(configFilePath);


        cache = new HTTPCache(apiConfig);

        client = new SSLClient(cache);
    }

    @Parameters({"excelName", "sheetName"})
    @BeforeTest
    public void readData(@Optional("") String excelName,
                         @Optional("") String sheetName) throws DocumentException {
        dataList = readExcelData(ApiDataBean.class, excelName.split(";"), sheetName.split(";"));
    }

    /**
     * 过滤数据，run标记为Y的执行。
     */
    @DataProvider(name = "apiData")
    public Iterator<Object[]> getApiData(ITestContext context)
            throws DocumentException {
        List<Object[]> dataProvider = new ArrayList<>();
        dataList.stream().filter(ApiDataBean::isRun).forEach(data -> dataProvider.add(new Object[]{data}));
        return dataProvider.iterator();
    }

    @Test(dataProvider = "apiData")
    public void apiTest(ApiDataBean apiDataBean) throws Exception {
        String response = client.sendRequest(apiDataBean);
        // 输出返回数据log
        ReportUtil.log("resp:" + response);
        // 验证预期信息
        verifyResult(response, apiDataBean.getVerify(), apiDataBean.isContains());
    }

    private void verifyResult(String sourceData, String verifyStr, boolean contains) {
        if (StringUtil.isEmpty(verifyStr)) {
            return;
        }
        String allVerify = ParamUtil.replaceParam(verifyStr, cache.getParamMap());
        ReportUtil.log("验证数据：" + allVerify);
        if (contains) {
            // 验证结果包含
            AssertUtil.contains(sourceData, allVerify);
        } else {
            // 通过';'分隔，通过jsonPath进行一一校验
            Pattern pattern = Pattern.compile("([^;]*)=([^;]*)");
            Assert.assertTrue(pattern.matcher(allVerify.trim()).find(), "没有找到合法格式的校验内容，合法格式：xx1=oo1;xx2=oo2;xx3=oo3");
            Matcher m = pattern.matcher(allVerify.trim());
            while (m.find()) {
                String actualValue = ParamUtil.replaceResult(sourceData, m.group(1));
                String exceptValue = ParamUtil.replaceResult(sourceData, m.group(2));
                ReportUtil.log(String.format("验证转换后的值%s=%s", actualValue, exceptValue));
                Assert.assertEquals(actualValue, exceptValue, "验证预期结果失败。");
            }
        }
    }

    private <T extends BaseBean> List<T> readExcelData(Class<T> clz,
                                                         String[] excelPathArr, String[] sheetNameArr)
            throws DocumentException {
        List<T> allExcelData = new ArrayList<>();

        List<T> temArrayList = new ArrayList<>();
        for (String excelPath : excelPathArr) {
            String filePath = Paths.get(System.getProperty("user.dir"), excelPath).toString();
            temArrayList.clear();
            if (sheetNameArr.length == 0 || StringUtil.isEmpty(sheetNameArr[0])) {
                temArrayList.addAll(ExcelUtil.readExcel(clz, filePath));
            } else {
                for (String sheetName : sheetNameArr) {
                    temArrayList.addAll(ExcelUtil.readExcel(clz, filePath, sheetName));
                }
            }
            temArrayList.forEach((bean) -> bean.setExcelName(excelPath));
            allExcelData.addAll(temArrayList);
        }
        return allExcelData;
    }
}
