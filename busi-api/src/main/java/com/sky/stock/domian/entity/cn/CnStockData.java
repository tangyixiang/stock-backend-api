package com.sky.stock.domian.entity.cn;

import com.sky.stock.domian.entity.cn.Id.UniqueId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity(name = "cn_stock_data")
@IdClass(UniqueId.class)
@NoArgsConstructor
@AllArgsConstructor
public class CnStockData {

    @Id
    private String symbol;

    @Id
    @Comment("日期")
    private LocalDate date;

    @Comment("开盘")
    private BigDecimal open;

    @Comment("收盘")
    private BigDecimal close;

    @Comment("最高")
    private BigDecimal high;

    @Comment("最低")
    private BigDecimal low;

    @Comment("交易量")
    private BigDecimal tradeVol;

    @Comment("交易额")
    private BigDecimal tradeQuota;

    @Comment("振幅")
    private BigDecimal amplitude;

    @Comment("涨幅")
    private BigDecimal diffPer;

    @Comment("涨跌额")
    private BigDecimal diffQuota;

    @Comment("换手率")
    private BigDecimal exchangeRate;

    public CnStockData(String symbol, LocalDate date, BigDecimal open, BigDecimal close, BigDecimal high, BigDecimal low) {
        this.symbol = symbol;
        this.date = date;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
    }
}
