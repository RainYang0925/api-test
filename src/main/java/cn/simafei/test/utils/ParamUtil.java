package cn.simafei.test.utils;

import com.alibaba.fastjson.JSONPath;
import org.testng.Assert;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fengpj on 2017/3/18.
 * 参数处理类
 */
public class ParamUtil {

    /**
     * 截取自定义方法正则表达式：__xxx(ooo)\\u4E00-\\u9FA5支持中文
     */
    private final static Pattern FUNC_PATTERN = Pattern.compile("__(\\w*?)\\((([^)]*,?)*)\\)");

    /**
     * 替换符，如果数据中包含“${}”则会被替换成公共参数中存储的数据
     */
    private final static Pattern REPLACE_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    private final static String ARGS_SPLIT = "(?<!\\\\),";

    /**
     * 组件预参数（处理__func()以及${}）
     * ps: 函数中不能嵌套函数，但是可以用动态参数${}
     */
    public static String replaceFunc(String param, Map<String, String> saveMap) {
        // 处理${}
        param = replaceParam(param, saveMap);
        Matcher m = FUNC_PATTERN.matcher(param);
        while (m.find()) {
            String funcName = m.group(1);
            String args = m.group(2);
            String value;
            if (FunctionUtil.isFunction(funcName)) {
                // 属于函数助手，调用那个函数助手获取。
                value = FunctionUtil.getValue(funcName, args.split(ARGS_SPLIT));
                // 解析对应的函数失败
                Assert.assertNotNull(value, String.format("解析函数失败：%s。", funcName));
                param = StringUtil.replaceFirst(param, m.group(), value);
            }
        }
        return param;
    }

    /**
     * 从保存的参数列表中替换原参数中的动态参数 ${}
     * ps: 动态参数中的key不能写入函数
     * @param param 参数字符串  a=b&b=c (form data) or {"a":"b", "c":"d"} (post body)
     * @param saveMap 保存的参数，包括自定义的参数和从服务器下载设置的参数
     * @return 替换动态参数的结构
     */
    public static String replaceParam(String param, Map<String, String> saveMap) {
        if (StringUtil.isEmpty(param)) {
            return "";
        }
        Matcher m = REPLACE_PATTERN.matcher(param);// 取公共参数正则
        while (m.find()) {
            String replaceKey = m.group(1);
            String value;
            // 从公共参数池中获取值
            value = saveMap.get(replaceKey);
            // 如果公共参数池中未能找到对应的值，该用例失败。
            Assert.assertNotNull(value, String.format("格式化参数失败，公共参数中找不到%s。", replaceKey));
            param = param.replace(m.group(), value);
        }
        return param;
    }

    /**
     * 通过JsonPath替换返回结果中的
     */
    public static String replaceResult(String sourceJson, String key) {
        key = key.trim();
        Matcher funMatch = FUNC_PATTERN.matcher(key);
        if (key.startsWith("$.") || key.startsWith("$[")) {// JsonPath
            key = JSONPath.read(sourceJson, key).toString();
        } else if (funMatch.find()) {
            String args = funMatch.group(2);
            String[] argArr = args.split(ARGS_SPLIT);
            for (int index = 0; index < argArr.length; index++) {
                String arg = argArr[index];
                if (arg.startsWith("$.") || arg.startsWith("$[")) {
                    argArr[index] = JSONPath.read(sourceJson, arg).toString();
                }
            }
            String value = FunctionUtil.getValue(funMatch.group(1), argArr);
            key = StringUtil.replaceFirst(key, funMatch.group(), value);

        }
        return key.trim();
    }
}
