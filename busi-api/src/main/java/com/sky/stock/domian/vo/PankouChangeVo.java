package com.sky.stock.domian.vo;

import com.sky.stock.domian.entity.cn.CnStockData;
import com.sky.stock.domian.entity.cn.PankouChange;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PankouChangeVo {

    private String symbol;

    private String name;

    private BigDecimal marketValue;

    private int count;

    private List<PankouChange> detail;

    private List<CnStockData> data;

}
