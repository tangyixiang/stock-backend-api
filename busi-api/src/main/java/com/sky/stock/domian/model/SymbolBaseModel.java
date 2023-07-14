package com.sky.stock.domian.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class SymbolBaseModel {

    protected String symbol;

    protected String name;

    protected BigDecimal marketValue;

    protected String description;
}
