package com.sky.stock.domian.model;

import lombok.Data;

import java.util.List;

@Data
public class PageResult {

    private long total;

    private List<?> list;

    public static PageResult of(long total, List<?> list) {
        PageResult pageResult = new PageResult();
        pageResult.setList(list);
        pageResult.setTotal(total);
        return pageResult;
    }
}
