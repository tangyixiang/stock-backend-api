package com.sky.stock.domian.entity.cn;

import com.sky.stock.domian.entity.cn.Id.UniqueId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Data
@Entity(name = "cn_stock_lhb")
@IdClass(value = UniqueId.class)
public class StockLHB {

    @Id
    @Comment("日期")
    private LocalDate date;
    @Id
    @Comment("代码")
    private String symbol;
    @Comment("名称")
    private String name;
    @Comment("解读")
    private String interpretation;
    @Comment("收盘")
    private double close;
    @Comment("涨跌幅")
    private double diffPer;
    @Comment("净买额")
    private double netBuyingAmount;
    @Comment("买入额")
    private double buyingAmount;
    @Comment("卖出额")
    private double sellingAmount;
    @Comment("成交额")
    private double turnover;
    @Comment("市场总成交额")
    private double totalMarketTurnover;
    @Comment("净买额占总成交比")
    private double netBuyingRatio;
    @Comment("成交额占总成交比")
    private double turnoverRatio;
    @Comment("换手率")
    private double turnoverRate;
    @Comment("流通市值")
    private double circulatingMarketValue;
    @Comment("上榜原因")
    private String reason;
    @Comment("上榜后1日")
    private double day1;
    @Comment("上榜后2日")
    private double day2;
    @Comment("上榜后5日")
    private double day5;
    @Comment("上榜后10日")
    private double day10;
}
