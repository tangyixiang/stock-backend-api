package com.sky.stock.domian.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class FutuUsData {

    private int code;
    private String message;
    private ResponseData data;

    @Data
    public static class ResponseData {
        private Pagination pagination;
        private List<StockInfo> list;
    }

    @Data
    public static class Pagination {
        private int page;
        private int pageSize;
        private int pageCount;
        private int total;
    }

    @Data
    public static class StockInfo {
        private Long stockId;
        private String name;
        @JsonProperty("stockCode")
        private String symbol;
        private String marketLabel;
        private String changeRatio;
        private String priceDirect;
        private String change;
        private String priceNominal;
        private String tradeTrunover;
        private String tradeVolumn;
        private String marketVal;
        private String circulationMarketValue;
        private String totalShares;
        private String circulationTotalShares;
        private String c_5Days;
        private String c_5Days_priceDirect;
        private String c_10Days;
        private String c_10Days_priceDirect;
        private String c_20Days;
        private String c_20Days_priceDirect;
        private String c_60Days;
        private String c_60Days_priceDirect;
        private String c_120Days;
        private String c_120Days_priceDirect;
        private String c_250Days;
        private String c_250Days_priceDirect;
        private String c_YearDays;
        private String c_YearDays_priceDirect;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StockInfo stockInfo = (StockInfo) o;
            return Objects.equals(stockId, stockInfo.stockId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stockId);
        }
    }

}