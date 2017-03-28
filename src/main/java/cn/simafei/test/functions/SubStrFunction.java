package cn.simafei.test.functions;

import cn.simafei.test.utils.StringUtil;

public class SubStrFunction implements Function {

    @Override
    public String execute(String[] args) {
        String source = args[0];
        int beginIndex = 0;
        if (StringUtil.isNotEmpty(args[1])) {
            beginIndex = Integer.parseInt(args[1]) - 1;
        }
        int endIndex = source.length();
        if (args.length > 2 && StringUtil.isNotEmpty(args[2])) {
            endIndex = Integer.parseInt(args[2]);
        }
        return source.substring(beginIndex, endIndex);
    }

    @Override
    public String getReferenceKey() {
        return "subStr";
    }


}
