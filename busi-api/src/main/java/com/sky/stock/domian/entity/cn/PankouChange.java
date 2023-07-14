package com.sky.stock.domian.entity.cn;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Entity(name = "pan_kou_change")
@Table(indexes = {
        @Index(name = "idx_pankouchange_symbol_time", columnList = "symbol, time"),
        @Index(name = "idx_pankouchange_type", columnList = "type")
})
public class PankouChange {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Comment("时间")
    private LocalDateTime time;

    @Comment("代码")
    private String symbol;

    @Comment("名称")
    private String name;

    @Comment("板块")
    private String type;

    @Comment("信息")
    private String message;

}
