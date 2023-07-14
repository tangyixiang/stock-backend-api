package com.sky.stock.domian.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OpenVolUp {

    private String symbol;

    /**
     * 上升比例
     */
    private Double riseRatio;

    private BigDecimal diffPer;

    private BigDecimal price;

    private BigDecimal marketValue;

    private BigDecimal tradeQuote;

}
