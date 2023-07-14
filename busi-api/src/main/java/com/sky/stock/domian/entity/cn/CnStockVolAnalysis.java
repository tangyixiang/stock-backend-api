package com.sky.stock.domian.entity.cn;

import com.sky.stock.domian.entity.cn.Id.UniqueId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Data
@IdClass(UniqueId.class)
@Entity(name = "cn_stock_vol_analysis")
@NoArgsConstructor
@AllArgsConstructor
public class CnStockVolAnalysis {

    @Id
    private String symbol;

    @Id
    private LocalDate date;

    @Comment("1 恐慌抛售  2 量升价涨")
    private Integer volType;

    @Comment("涨跌幅")
    private Double diffPer;

    @Comment("量比")
    private Double tradeVolPct;
}
