package com.sky.stock.domian.param;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PageQuery {

    protected int pageSize;

    protected int pageNo;

    protected LocalDate date;
}
