package com.sky.stock.domian.entity.cn;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Data
@Entity(name = "trade_day")
@NoArgsConstructor
public class TradeDay {


    @Id
    @Comment("交易日")
    private LocalDate date;

    public TradeDay(LocalDate date) {
        this.date = date;
    }
}
