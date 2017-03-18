package cn.simafei.test.functions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.simafei.test.utils.StringUtil;

public class DateFunction implements Function {

    @Override
    public String execute(String[] args) {
        Date date = new Date();
        if (args.length > 0 && StringUtil.isNotEmpty(args[0])) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, Integer.valueOf(args[0]));
            date = calendar.getTime();
        }
        if (args.length > 1 && args[1] != "") {
            SimpleDateFormat format = new SimpleDateFormat(args[1]);
            return format.format(date);
        } else {
            return String.format("%s", date.getTime());
        }
    }

    @Override
    public String getReferenceKey() {
        return "date";
    }

}
