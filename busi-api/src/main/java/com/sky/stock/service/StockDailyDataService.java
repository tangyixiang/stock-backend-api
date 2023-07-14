package com.sky.stock.service;

import com.sky.stock.data.Request;
import com.sky.stock.domian.entity.cn.StockDailyData;
import com.sky.stock.repository.StockDailyDataRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StockDailyDataService {

    private StockDailyDataRepository repository;

    public List<StockDailyData> syncCurrentData() {
        Table todayTable = Request.data("stock_zh_a_spot_em");
        Table table = todayTable.selectColumns("代码", "最新价", "成交量", "涨跌幅");
        table = table.dropWhere(table.doubleColumn("最新价").isMissing());
        LocalDateTime now = LocalDateTime.now();
        List<StockDailyData> list = new ArrayList<>();
        for (Row row : table) {
            String symbol = row.getString("代码");
            double price = row.getDouble("最新价");
            int tradeVol = row.getInt("成交量");
            double diffPer = row.getDouble("涨跌幅");
            StockDailyData stockDailyData = new StockDailyData(symbol, now, new BigDecimal(price), tradeVol, new BigDecimal(diffPer));
            list.add(stockDailyData);
        }
        repository.saveAll(list);
        return list;
    }
}
