package com.sky.stock.helper;

import java.math.BigDecimal;

public class MathHelper {

    public static double round(double data, int round) {
        BigDecimal bd = new BigDecimal(data);
        double result = bd.setScale(round, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result;
    }

    public static BigDecimal round(double data) {
        BigDecimal bd = new BigDecimal(data);
        return bd.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static int marketValueToYi(BigDecimal value) {
        return value.divide(new BigDecimal(10000 * 10000)).intValue();
    }

    public static Double strToNumber(String marketVal) {
        Double value;
        if (marketVal.contains("万亿")) {
            value = Double.parseDouble(marketVal.replace("万亿", "")) * 10000 * 10000 * 10000;
        } else if (marketVal.contains("亿")) {
            value = Double.parseDouble(marketVal.replace("亿", "")) * 10000 * 10000;
        } else if (marketVal.contains("万")) {
            value = Double.parseDouble(marketVal.replace("万", "")) * 10000;
        } else {
            value = Double.parseDouble(marketVal);
        }
        return value;
    }
}
