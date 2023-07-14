package com.sky.stock.domian.records;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StockRecord {

    private Long stockId;
    private String symbol;

    public StockRecord(Long stockId, String symbol) {
        this.stockId = stockId;
        this.symbol = symbol;
    }
}
