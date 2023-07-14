package com.sky.stock.domian.entity.us;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

@Data
@Entity(name = "us_stock_info")
public class UsStockInfo {

    @Id
    private Long stockId;

    @Comment("代码")
    private String symbol;

    @Comment("名称")
    private String name;

    @Comment("市场类型")
    private String marketLabel;

    @Comment("市值")
    private BigDecimal marketValue;

    @Comment("主营业务")
    @Column(columnDefinition = "text")
    private String description;

}