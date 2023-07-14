package com.sky.stock.helper;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

@Component
@AllArgsConstructor
public class TableHelper {

    private JdbcTemplate jdbcTemplate;

    public Table readSql(String sql) {
        return jdbcTemplate.query(sql, rs -> {
            return Table.read().db(rs);
        });
    }
}
