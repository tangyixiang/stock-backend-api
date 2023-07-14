package com.sky.stock.domian.entity.cn;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Entity(name = "cn_stock_tick_data")
@Table(indexes = {
        @Index(name = "idx_cnstocktickdata_symbol", columnList = "symbol, time")
})
public class CnStockTickData {

    @Id
    @GeneratedValue
    private Long id;

    @Comment("代码")
    private String symbol;

    @Comment("成交时间")
    private LocalDateTime time;

    @Comment("成交价格")
    private BigDecimal price;

    @Comment("价格变动")
    private BigDecimal diff;

    @Comment("成交量")
    private BigDecimal tradeVol;

    @Comment("成交金额")
    private BigDecimal tradeQuota;

    @Comment("性质")
    private String type;
}
