package com.sky.stock.domian.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RequestModel {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockModel {
        String symbol;
        String start_date;
        String end_date;
        String period;
        String adjust;

        public StockModel withSymbol(String newSymbol) {
            return new StockModel(newSymbol, start_date, end_date, period, adjust);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsStockModel {
        String symbol;
        String start_date;
        String end_date;

        public UsStockModel withSymbol(String newSymbol) {
            return new UsStockModel(newSymbol, start_date, end_date);
        }
    }
}