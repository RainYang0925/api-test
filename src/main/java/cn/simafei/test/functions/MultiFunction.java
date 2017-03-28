package cn.simafei.test.functions;

import java.math.BigDecimal;

public class MultiFunction implements Function {

    @Override
    public String execute(String[] args) {
        BigDecimal value = new BigDecimal(1);
        for (String numeric : args) {
            value = value.multiply(new BigDecimal(numeric));
        }
        return String.valueOf(value);
    }

    @Override
    public String getReferenceKey() {
        return "multi";
    }

}
