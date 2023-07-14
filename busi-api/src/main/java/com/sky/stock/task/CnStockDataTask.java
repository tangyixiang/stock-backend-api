package com.sky.stock.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.sky.stock.data.DataHandle;
import com.sky.stock.data.Request;
import com.sky.stock.data.RunningStatusData;
import com.sky.stock.domian.entity.cn.*;
import com.sky.stock.domian.model.RequestModel;
import com.sky.stock.helper.MathHelper;
import com.sky.stock.helper.TradeDayHelper;
import com.sky.stock.repository.PankouChangeRepository;
import com.sky.stock.repository.StockLHBRepository;
import com.sky.stock.repository.TradeDayRepository;
import com.sky.stock.service.*;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(name = "task.enable", havingValue = "true")
public class CnStockDataTask {

    private CnStockInfoService stockInfoService;

    private CnStockDataService stockDataService;

    private TradeDayRepository tradeDayRepository;

    private CnStockIndustryService industryService;

    private CnStockVolAnalysisService volAnalysisService;

    private TradeDayHelper tradeDayHelper;

    private StockDailyDataService stockDailyDataService;

    private StockLHBRepository stockLHBRepository;

    private PankouChangeRepository pankouChangeRepository;

    @PostConstruct
    public void init() {
        log.info("定时任务开启");
    }

    @Scheduled(cron = "0 10 15 ? * MON-FRI")
    public void syncSymbol() {
        log.info("CN -> 开始同步symbol");
        Table todayTable = Request.data("stock_zh_a_spot_em");
        Table table = todayTable.selectColumns("代码", "名称", "总市值");
        table = table.where(table.stringColumn("名称").eval((s1, s2) -> !s1.contains("退") && !s2.contains("退"), "退").or(table.numberColumn("总市值").isNotMissing()));
        List<CnStockInfo> stockInfoList = stockInfoService.list();
        Column<?> codes = table.column("代码");
        List<String> allSymbolList = codes.asList().stream().map(data -> data.toString()).toList();
        List<String> existSymbolList = stockInfoList.stream().map(CnStockInfo::getSymbol).toList();
        // 新上市股票
        List<String> newSymbolList = allSymbolList.stream().filter(symbol -> !existSymbolList.contains(symbol)).toList();
        // 退市股票
        List<String> delistList = existSymbolList.stream().filter(symbol -> !allSymbolList.contains(symbol)).toList();
        // 删除
        stockInfoService.getRepository().deleteAllById(delistList);

        stockDataService.getRepository().removeBySymbolIn(delistList);

        List<Object[]> tableRow = DataHandle.getTableRow(table);
        List<String> fileds = List.of("symbol", "name", "marketValue");
        List<CnStockInfo> cnStockInfoList = DataHandle.transferObject(CnStockInfo.class, fileds, tableRow);
        for (CnStockInfo cnStockInfo : cnStockInfoList) {
            CnStockInfo db = stockInfoService.getRepository().findBySymbol(cnStockInfo.getSymbol());
            if (db != null) {
                stockInfoService.getRepository().updateDataByTask(cnStockInfo.getName(), cnStockInfo.getMarketValue(), cnStockInfo.getSymbol());
            } else {
                stockInfoService.getRepository().save(cnStockInfo);
            }
        }

        newSymbolList.forEach(symbol -> {
            try {
                Table table2 = Request.data("stock_zyjs_ths", Map.of("symbol", symbol));
                String description = String.valueOf(table2.column("主营业务").get(0));
                stockInfoService.getRepository().findById(symbol).ifPresent(data -> stockInfoService.getRepository().updateDescriptionBySymbol(description, symbol));

            } catch (Exception e) {
                log.error("异常,symbol:{}", symbol);
            }
        });
        log.info("同步symbol结束");
    }

    @Scheduled(cron = "0 30 15 ? * MON-FRI")
    public void openDayData() {
        String today = DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        log.info("开始同步数据,日期:{}", today);
        RequestModel.StockModel stockDataModel = new RequestModel.StockModel("000001", today, today, "daily", "qfq");
        Table table = Request.data("stock_zh_a_hist", BeanUtil.beanToMap(stockDataModel));
        if (table != null) {
            log.info("今日是交易日");
            tradeDayRepository.save(new TradeDay(LocalDate.now()));
        }
        List<CnStockInfo> stockInfoList = stockInfoService.list();
        stockInfoList.parallelStream().forEach(stock -> DataHandle.ignoreException(() -> stockDataService.saveStockData(stockDataModel.withSymbol(stock.getSymbol())), stock.getSymbol() + "数据同步异常"));

        log.info("同步数据完成");
    }

    @Scheduled(cron = "0 40 15 ? * MON-FRI")
    public void todayIndustry() {
        String today = DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        log.info("开始同步行业板块,日期:{}", today);
        if (!tradeDayHelper.todayIsTrade()) return;
        Table table = Request.data("stock_board_industry_name_em");
        List<Object[]> tableRow = DataHandle.getTableRow(table);
        List<String> fields = List.of("rank", "industryName", "industryCode", "price", "diffQuota", "diffPer", "marketValue", "exchangeRate", "upNum", "downNum", "leaderName", "leaderPer");
        List<CnStockIndustry> stockIndustryList = DataHandle.transferObject(CnStockIndustry.class, fields, tableRow);
        stockIndustryList.forEach(industry -> industry.setDate(today));
        industryService.getRepository().saveAll(stockIndustryList);
        log.info("同步行业板块完成");
    }

    @Scheduled(cron = "0 42 15 ? * MON-FRI")
    public void volTodayCalculate() {
        log.info("开始计算今日成交量量比");
        if (!tradeDayHelper.todayIsTrade()) return;
        List<CnStockInfo> stockInfoList = stockInfoService.list();
        for (CnStockInfo cnStockInfo : stockInfoList) {
            List<CnStockData> list = stockDataService.findFixedData(cnStockInfo.getSymbol(), 50);
            Table table = DataHandle.toTable(list, Map.of("tradeVol", ColumnType.DOUBLE));
            if (table.rowCount() == 0) continue;
            DoubleColumn pctColumn = table.doubleColumn("tradeVol").pctChange().setName("tradeVolPct");
            table.addColumns(pctColumn);
            Table table2 = table.where(table.doubleColumn("tradeVolPct").isGreaterThan(0.15).and(table.dateColumn("date").isEqualTo(LocalDate.now())));
            if (table2.rowCount() > 0) {
                double diffPer = table2.row(table2.rowCount() - 1).getDouble("diffPer");
                double tradeVolPct = MathHelper.round(table2.row(table2.rowCount() - 1).getDouble("tradeVolPct"), 2);
                int volType = (diffPer < 0) ? 1 : ((diffPer > 3.7) ? 2 : 0);
                if (volType != 0) {
                    CnStockVolAnalysis cnStockVolAnalysis = new CnStockVolAnalysis(cnStockInfo.getSymbol(), LocalDate.now(), volType, diffPer, tradeVolPct);
                    volAnalysisService.getRepository().save(cnStockVolAnalysis);
                }
            }
        }
        log.info("计算今日成交量量比结束");
    }

    @Scheduled(cron = "0 00/1 9-15 ? * MON-FRI")
    public void openDayAmVolCompare() {
        LocalTime now = LocalTime.now();
        LocalTime open = LocalTime.of(9, 30);
        LocalTime noon = LocalTime.of(11, 30);
        LocalTime afternoon = LocalTime.of(13, 00);
        LocalTime end = LocalTime.of(15, 00);

        if (now.isBefore(open) || (now.isAfter(noon) && now.isBefore(afternoon)) || now.isAfter(end)) {
            log.info("休息时间");
        } else {
            log.info("同步最新价格");
            List<StockDailyData> stockDailyData = stockDailyDataService.syncCurrentData();
            volAnalysisService.compareLastDayVol(stockDailyData);
            long downCount = stockDailyData.stream().filter((data) -> data.getDiffPer().doubleValue() < 0.0).count();
            long upCount = (long) stockDailyData.size() - downCount;
            RunningStatusData.setUpAndDownNum(Map.of("up", (int) upCount, "down", (int) downCount));
            log.info("同步最新价格,量比计算结束");
        }
    }

    @Scheduled(cron = "0 30 16 ? * MON-FRI")
    public void lhb() {
        log.info("同步龙虎榜");
        if (!tradeDayHelper.todayIsTrade()) return;
        String today = DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        Table table = Request.data("stock_lhb_detail_em", Map.of("start_date", today, "end_date", today));
        if (table == null) {
            return;
        }
        table = table.removeColumns(0);
        List<String> fields = List.of("symbol", "name", "date", "interpretation", "close", "diffPer",
                "netBuyingAmount", "buyingAmount", "sellingAmount", "turnover", "totalMarketTurnover", "netBuyingRatio",
                "turnoverRatio", "turnoverRate", "circulatingMarketValue", "reason", "day1", "day2", "day5", "day10"
        );
        List<Object[]> tableRow = DataHandle.getTableRow(table);
        List<StockLHB> lhbList = DataHandle.transferObject(StockLHB.class, fields, tableRow);
        stockLHBRepository.saveAll(lhbList);
        log.info("同步龙虎榜结束");
    }

    @Scheduled(cron = "0 35 15 ? * MON-FRI")
    public void pankou() {
        log.info("同步盘口异动");
        if (!tradeDayHelper.todayIsTrade()) return;
        Table table1 = Request.data("stock_changes_em", Map.of("symbol", "大笔买入"));
        Table table2 = Request.data("stock_changes_em", Map.of("symbol", "有大买盘"));
        Table table3 = Request.data("stock_changes_em", Map.of("symbol", "火箭发射"));

        table1.append(table2).append(table3);
        List<String> fields = List.of("time", "symbol", "name", "type", "message");
        List<Object[]> tableRow = DataHandle.getTableRow(table1);
        List<PankouChange> pankouList = DataHandle.transferObject(PankouChange.class, fields, tableRow);

        pankouChangeRepository.saveAll(pankouList);
    }


}
