package cn.simafei.test.functions;

import java.math.BigDecimal;

public class MaxFunction implements Function {

    @Override
    public String execute(String[] args) {
        BigDecimal maxValue = new BigDecimal(args[0]);
        for (String numSerial : args) {
            maxValue = maxValue.max(new BigDecimal(numSerial));
        }
        return String.valueOf(maxValue);
    }

    @Override
    public String getReferenceKey() {
        return "max";
    }

}
