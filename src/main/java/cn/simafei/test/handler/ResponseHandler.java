package cn.simafei.test.handler;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author fengpj
 * @version 1.0
 * @since 2017/5/8
 */
public interface ResponseHandler {

    /**
     * 从字符串中抽取要保存的参数
     * @param response Http返回结果
     * @param save 要save的key
     */
    Map<String, Object> extractSaveMap(String response, String save);
}
