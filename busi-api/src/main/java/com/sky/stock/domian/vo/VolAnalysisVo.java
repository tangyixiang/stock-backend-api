package com.sky.stock.domian.vo;

import com.sky.stock.domian.entity.cn.CnStockData;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VolAnalysisVo {

    private long total;

    private List<AnalysisData> list;

    @Data
    public static class AnalysisData {

        private String symbol;

        private String name;

        private Double tradeVolPct;

        private BigDecimal marketValue;

        private List<CnStockData> data;
    }
}
