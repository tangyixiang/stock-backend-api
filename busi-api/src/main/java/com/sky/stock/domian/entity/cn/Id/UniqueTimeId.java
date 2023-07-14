package com.sky.stock.domian.entity.cn.Id;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 唯一主键
 */
@Data
public class UniqueTimeId implements Serializable {

    /**
     * symbol
     */
    private String symbol;

    /**
     * 日期
     */
    private LocalDateTime time;

    // 重写 equals 和 hashCode 方法，用于 JPA 根据主键进行查找和比较
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UniqueTimeId)) return false;
        UniqueTimeId that = (UniqueTimeId) o;
        return Objects.equals(time, that.time) &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, symbol);
    }
}
