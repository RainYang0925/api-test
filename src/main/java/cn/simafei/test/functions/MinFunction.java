package cn.simafei.test.functions;

import java.math.BigDecimal;

public class MinFunction implements Function {

    @Override
    public String execute(String[] args) {
        BigDecimal maxValue = new BigDecimal(args[0]);
        for (String numSerial : args) {
            maxValue = maxValue.min(new BigDecimal(numSerial));
        }
        return String.valueOf(maxValue);
    }

    @Override
    public String getReferenceKey() {
        return "min";
    }

}
