package com.sky.stock.helper;

public class MarketHelper {

    public static String getSymbolOfMarket(String symbol) {
        String type = "";
        if (symbol.startsWith("60")) {
            type = "SH";
        } else if (symbol.startsWith("688")) {
            type = "SH";
        } else if (symbol.startsWith("900")) {
            type = "SH";
        } else if (symbol.startsWith("00")) {
            type = "SZ";
        } else if (symbol.startsWith("300")) {
            type = "SZ";
        } else if (symbol.startsWith("200")) {
            type = "SZ";
        }
        return type;
    }
}
