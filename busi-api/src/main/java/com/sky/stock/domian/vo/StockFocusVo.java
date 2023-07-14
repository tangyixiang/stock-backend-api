package com.sky.stock.domian.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StockFocusVo {

    private String symbol;

    /**
     * 第一次关注
     */
    private LocalDate firstFocusDate;

    /**
     * 最后一次关注
     */
    private LocalDate lastFocusDate;

    /**
     * 今年涨幅
     */
    private double diffPerOfYear;
}
