package com.sky.stock.domian.entity.cn;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
@Setter
@Data
@Entity(name = "stock_focus")
@Table(indexes = {
        @Index(name = "idx_symbol", columnList = "symbol")
})
public class StockFocus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String symbol;

    @Comment("关注日期")
    private LocalDate date;

    @Comment("来源")
    private String source;

}
