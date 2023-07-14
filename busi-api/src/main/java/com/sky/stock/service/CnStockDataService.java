package com.sky.stock.service;

import cn.hutool.core.bean.BeanUtil;
import com.sky.stock.data.DataHandle;
import com.sky.stock.data.Request;
import com.sky.stock.domian.entity.cn.CnStockData;
import com.sky.stock.domian.entity.cn.CnStockInfo;
import com.sky.stock.domian.entity.cn.TradeDay;
import com.sky.stock.domian.model.RequestModel;
import com.sky.stock.domian.model.SymbolGains;
import com.sky.stock.repository.CnStockDataRepository;
import com.sky.stock.repository.TradeDayRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter
@AllArgsConstructor
public class CnStockDataService {

    private CnStockDataRepository repository;

    private CnStockInfoService stockInfoService;

    private TradeDayRepository tradeDayRepository;

    /**
     * 数据同步
     */
    public void saveStockData(RequestModel.StockModel stockDataModel) {
        log.info("同步数据:{}", stockDataModel.toString());
        Table table = Request.data("stock_zh_a_hist", BeanUtil.beanToMap(stockDataModel));
        if (table != null) {
            log.info("保存数据:{}", stockDataModel.getSymbol());
            int count = table.rowCount();
            String[] symbolData = new String[count];
            Arrays.fill(symbolData, stockDataModel.getSymbol());
            table.insertColumn(0, StringColumn.create("代码", symbolData));
            List<Object[]> tableRow = DataHandle.getTableRow(table);
            List<String> fields = List.of("symbol", "date", "open", "close", "high", "low", "tradeVol", "tradeQuota", "amplitude", "diffPer", "diffQuota", "exchangeRate");
            List<CnStockData> cnStockDataList = DataHandle.transferObject(CnStockData.class, fields, tableRow);
            repository.saveAll(cnStockDataList);
        }
        log.info("同步完成:{}", stockDataModel.getSymbol());
    }


    /**
     * 获取有限数据
     *
     * @param limit 限制数量
     * @return
     */
    public List<CnStockData> findFixedData(String symbol, int limit) {
        List<CnStockData> list = repository.findFixedData(symbol, limit);
        return list;
    }

    /**
     * 涨跌幅
     *
     * @param type        类型
     * @param period      周期
     * @param greaterZero 涨/跌
     * @return
     */
    public TreeSet<SymbolGains> getGains(String type, int period, Boolean greaterZero) {
        long start2 = System.currentTimeMillis();
        log.info("排行榜运行");
        TradeDay maxDate = tradeDayRepository.findMaxDate();
        LocalDate startDate;
        LocalDate endDate;
        if ("d".equals(type)) {
            startDate = tradeDayRepository.periodDate(period).getDate();
        } else {
            int year = LocalDate.now().getYear() - (period - 1);
            startDate = tradeDayRepository.firstDateInYear(LocalDate.of(year, 1, 1)).getDate();
        }
        endDate = maxDate.getDate();
        log.info("开始日期:{},结束日期:{}", startDate, endDate);
        List<CnStockData> startData = repository.findOHLCByDate(startDate);
        List<CnStockData> endData = repository.findOHLCByDate(endDate);
        List<String> currentSymbolList = endData.stream().map(data -> data.getSymbol()).toList();

        ArrayList<CnStockData> cnStockData = new ArrayList<>();
        // 去掉最近一个交易日已经不存在的数据
        startData = startData.stream().filter(data -> currentSymbolList.contains(data.getSymbol())).toList();
        cnStockData.addAll(startData);
        cnStockData.addAll(endData);

        TreeSet<SymbolGains> symbolGainsList = new TreeSet<>(Comparator.comparing(SymbolGains::getDiffPer).reversed());
        Map<String, List<CnStockData>> collect = cnStockData.stream().collect(Collectors.groupingBy(CnStockData::getSymbol));
        log.info("开始计算");
        collect.keySet().stream().forEach(symbol -> {
            List<CnStockData> list = collect.get(symbol);
            List<CnStockData> dataList = list.stream().sorted(Comparator.comparing(CnStockData::getDate)).toList();
            // 说明今年
            CnStockData start;
            CnStockData end;
            if (list.size() == 1) {
                log.info("当天没有数据:{}", symbol);
                start = repository.findBySymbolLeDate(symbol, startDate);
                end = dataList.get(0);
            } else {
                start = dataList.get(0);
                end = dataList.get(1);
            }
            double diffPer = end.getClose().subtract(start.getOpen()).divide(start.getOpen(), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100))
                    .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            // 过滤不满足条件的
            boolean skipFlag = greaterZero ? diffPer <= 0 : diffPer > 0;
            if (skipFlag) {
                return;
            }
            CnStockInfo cnStockInfo = stockInfoService.findBySymbol(symbol);
            if (cnStockInfo == null) {
                log.info("symbol不存在:{}", symbol);
                return;
            }
            SymbolGains gains = new SymbolGains();
            gains.setSymbol(symbol).setDiffPer(diffPer).setName(cnStockInfo.getName()).setMarketValue(cnStockInfo.getMarketValue()).setDescription(cnStockInfo.getDescription());
            symbolGainsList.add(gains);
        });
        long end2 = System.currentTimeMillis();
        log.info("计算结束,{}", (end2 - start2));
        return symbolGainsList;
    }

    @Cacheable(value = "data.last.day")
    public Map<String, CnStockData> lastDayData() {
        String maxDate = repository.findMaxDate();
        List<CnStockData> list = repository.findByDate(LocalDate.parse(maxDate));
        HashMap<String, CnStockData> map = new HashMap<>();
        for (CnStockData cnStockData : list) {
            map.put(cnStockData.getSymbol(), cnStockData);
        }
        return map;
    }
}