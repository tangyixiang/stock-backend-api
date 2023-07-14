package com.sky.stock.domian.param;

import lombok.Data;

@Data
public class StockInfoQuery extends PageQuery {

    private Integer min;

    private Integer max;

    private String symbol;
}
