package cn.simafei.test.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author fengpj
 * @version 1.0
 * @since 2017/5/8
 */
public class TextResponseHandler implements ResponseHandler {
    @Override
    public Map<String, Object> extractSaveMap(String response, String save) {
        Map<String, Object> saveMap = new HashMap<>();
        saveMap.put(save, response);
        return saveMap;
    }
}
