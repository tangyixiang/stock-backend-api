package com.sky.stock.domian.entity.cn.Id;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class UniqueIndustryId implements Serializable {

    private String date;

    private String industryCode;

    // 重写 equals 和 hashCode 方法，用于 JPA 根据主键进行查找和比较
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UniqueIndustryId)) return false;
        UniqueIndustryId that = (UniqueIndustryId) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(industryCode, that.industryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, industryCode);
    }
}