package com.sky.stock.enums;

import java.util.stream.Stream;

public enum MarketValueScope {

    M0_50(1, "0~50亿"),
    M50_100(2, "50~100亿"),
    M100_200(3, "100~200亿"),
    M200_500(4, "200~500亿"),
    M500_1000(5, "500~1000亿"),
    M1000_5000(6, "1000~5000亿"),
    M5000_MAX(7, "5000亿以上");

    private int code;

    private String desc;

    MarketValueScope(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(int code) {
        return Stream.of(MarketValueScope.values())
                .filter(s -> s.getCode() == code)
                .findFirst().map(MarketValueScope::getDesc).orElse("");
    }
}
