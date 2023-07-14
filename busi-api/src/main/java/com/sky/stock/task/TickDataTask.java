package com.sky.stock.task;

import com.sky.stock.data.DataHandle;
import com.sky.stock.data.Request;
import com.sky.stock.domian.entity.cn.CnStockTickData;
import com.sky.stock.helper.MarketHelper;
import com.sky.stock.helper.TradeDayHelper;
import com.sky.stock.repository.CnStockTickDataRepository;
import com.sky.stock.repository.PankouChangeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(name = "task.enable", havingValue = "true")
public class TickDataTask {

    private TradeDayHelper tradeDayHelper;

    private PankouChangeRepository pankouChangeRepository;

    private CnStockTickDataRepository tickDataRepository;

    //@Scheduled(cron = "0 55 16 ? * MON-FRI")
    public void syncCnTickData() {
        log.info("同步tick级别数据");
        if (!tradeDayHelper.todayIsTrade()) return;
        LocalDate day = tradeDayHelper.getLatelyDay();
        LocalDateTime startTime = LocalDateTime.of(day, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(day, LocalTime.MAX);
        List<String> symbolList = pankouChangeRepository.findSymbolByDate(startTime, endTime);
        for (String symbol : symbolList) {
            log.info("{},开始同步tick data", symbol);
            String type = MarketHelper.getSymbolOfMarket(symbol);
            Table table = Request.data("stock_zh_a_tick_tx_js", Map.of("symbol", type.toLowerCase() + symbol));

            List<String> fields = List.of("time", "price", "diff", "tradeVol", "tradeQuota", "type");
            List<Object[]> tableRow = DataHandle.getTableRow(table);
            List<CnStockTickData> stockTickDataList = DataHandle.transferObject(CnStockTickData.class, fields, tableRow);
            stockTickDataList.forEach(data -> data.setSymbol(symbol));

            tickDataRepository.saveAll(stockTickDataList);
        }
    }
}
