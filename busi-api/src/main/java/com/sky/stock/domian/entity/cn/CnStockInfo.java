package com.sky.stock.domian.entity.cn;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

@Data
@Entity(name = "cn_stock_info")
public class CnStockInfo {

    @Id
    private String symbol;

    @Comment("名称")
    private String name;

    @Comment("主营业务")
    private String description;

    @Comment("市值")
    private BigDecimal marketValue;

}
