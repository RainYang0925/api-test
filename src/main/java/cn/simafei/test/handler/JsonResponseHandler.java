package cn.simafei.test.handler;

import cn.simafei.test.utils.ParamUtil;
import cn.simafei.test.utils.ReportUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 *
 * @author fengpj
 * @version 1.0
 * @since 2017/5/8
 */
public class JsonResponseHandler implements ResponseHandler {
    @Override
    public Map<String, Object> extractSaveMap(String response, String save) {
        Map<String, Object> saveMap = new HashMap<>();
        if (null == response || "".equals(response)
                || null == save || "".equals(save)) {
            return saveMap;
        }

        String[] saves = save.split(";");
        String key, value;
        for (String saveKey : saves) {
            Pattern pattern = Pattern.compile("([^;=]*)=([^;]*)");
            Matcher m = pattern.matcher(saveKey.trim());
            while (m.find()) {
                key = ParamUtil.replaceResult(response, m.group(1));
                value = ParamUtil.replaceResult(response, m.group(2));
                saveMap.put(key, value);
            }
        }
        return saveMap;
    }
}
