package cn.simafei.test.handler;

import cn.simafei.test.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author fengpj
 * @version 1.0
 * @since 2017/5/8
 */
public class ResponseHandlerFactory {

    public static ResponseHandler createFromStrType(String str) {
        if (StringUtil.isJSON(str)) {
            return new JsonResponseHandler();
        } else if (StringUtil.isXML(str)) {
            return new XMLResponseHandler();
        } else {
            return new TextResponseHandler();
        }
    }
}
