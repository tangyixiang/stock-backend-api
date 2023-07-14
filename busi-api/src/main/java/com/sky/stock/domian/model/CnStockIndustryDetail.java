package com.sky.stock.domian.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 板块明细
 */
@Data
public class CnStockIndustryDetail {

    private String symbol;
    private String name;
    private BigDecimal price;
    private BigDecimal diffPer;
    private BigDecimal diffQuota;
    private BigDecimal tradeVol;
    private BigDecimal tradeQuota;
    private BigDecimal amplitude;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal open;
    private BigDecimal yesterdayClose;
    private BigDecimal exchangeRate;
    private BigDecimal pe;
    private BigDecimal pb;
}
