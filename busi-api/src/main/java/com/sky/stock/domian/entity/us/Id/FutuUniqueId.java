package com.sky.stock.domian.entity.us.Id;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 唯一主键
 */
@Data
@Embeddable
public class FutuUniqueId implements Serializable {

    /**
     * stockId
     */
    private Long stockId;
    /**
     * symbol
     */
    private String symbol;

    /**
     * 日期
     */
    private LocalDate date;
}
