package cn.simafei.test.functions;

import cn.simafei.test.utils.StringUtil;

public class ReplaceFunction implements Function {

    @Override
    public String execute(String[] args) {
        String source = args[0];
        String regex = args[1];
        String replacement = "";
        if (args.length > 2 && StringUtil.isNotEmpty(args[2])) {
            replacement = args[2];
        }
        return source.replaceAll(regex, replacement);
    }

    @Override
    public String getReferenceKey() {
        return "replace";
    }

}
