package cn.simafei.test.functions;

import cn.simafei.test.utils.StringUtil;

public class AddStrFunction implements Function {

    @Override
    public String execute(String[] args) {
        StringBuffer value = new StringBuffer();
        for (String arg : args) {
            if (StringUtil.isEmpty(arg)) {
                arg = ",";
            }
            value.append(arg);
        }
        return value.toString();
    }

    @Override
    public String getReferenceKey() {
        return "addStr";
    }

}
