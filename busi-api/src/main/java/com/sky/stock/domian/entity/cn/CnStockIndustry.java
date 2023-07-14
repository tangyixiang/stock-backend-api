package com.sky.stock.domian.entity.cn;

import com.sky.stock.domian.entity.cn.Id.UniqueIndustryId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;
import org.hibernate.annotations.Comment;

@Data
@Entity(name = "cn_stock_industry")
@IdClass(UniqueIndustryId.class)
public class CnStockIndustry {

    @Id
    private String date;

    @Id
    @Comment("板块代码")
    private String industryCode;

    @Comment("排名")
    private Long rank;

    @Comment("板块名称")
    private String industryName;

    @Comment("最新价")
    private Double price;

    @Comment("涨跌额")
    private Double diffQuota;

    @Comment("涨跌幅")
    private Double diffPer;

    @Comment("市值")
    private Long marketValue;

    @Comment("换手率")
    private Double exchangeRate;

    @Comment("上涨数量")
    private Long upNum;

    @Comment("下跌数量")
    private Long downNum;

    @Comment("领涨")
    private String leaderName;

    @Comment("领涨-涨跌幅")
    private Double leaderPer;


}
