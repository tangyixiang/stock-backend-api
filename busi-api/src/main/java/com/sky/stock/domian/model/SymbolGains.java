package com.sky.stock.domian.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 收益
 */
@Data
@Accessors(chain = true)
public class SymbolGains extends SymbolBaseModel implements Comparable<SymbolGains> {

    private String symbol;

    /**
     * 涨幅
     */
    private double diffPer;


    @Override
    public int compareTo(SymbolGains o) {
        if (diffPer == o.getDiffPer()){
            return 0;
        }
        return diffPer > o.getDiffPer() ? -1 : 1;

    }
}
