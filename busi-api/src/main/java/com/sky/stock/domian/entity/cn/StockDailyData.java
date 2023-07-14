package com.sky.stock.domian.entity.cn;

import com.sky.stock.domian.entity.cn.Id.UniqueTimeId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "stock_daily_data")
@IdClass(UniqueTimeId.class)
public class StockDailyData {

    @Id
    private String symbol;

    @Id
    @Comment("时间")
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime time;
    @Comment("价格")
    private BigDecimal price;
    @Comment("交易量")
    private Integer tradeVol;
    @Transient
    @Comment("涨跌幅")
    private BigDecimal diffPer;

}
