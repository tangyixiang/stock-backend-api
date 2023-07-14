package com.sky.stock.service;

import com.sky.stock.domian.entity.cn.CnStockData;
import com.sky.stock.helper.BarHelper;
import com.sky.stock.helper.TableHelper;
import com.sky.stock.repository.TradeDayRepository;
import lombok.AllArgsConstructor;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TechAnalysisService {

    private CnStockDataService stockDataService;

    private CnStockInfoService stockInfoService;

    private TradeDayRepository tradeDayRepository;

    private TableHelper tableHelper;

    public List<String> fiveDaysBreakthrough() {
        Map<String, CrossedUpIndicatorRule> ruleMap = new ConcurrentHashMap<>();
        List<String> symbolList = new ArrayList<>();
        LocalDate endDate = tradeDayRepository.findMaxDate().getDate();
        LocalDate startDate = endDate.minusDays(60);

        List<CnStockData> cnStockDataList = stockDataService.getRepository().getDataBetween(startDate, endDate);
        Map<String, List<CnStockData>> map = cnStockDataList.stream().collect(Collectors.groupingBy(CnStockData::getSymbol));

        ProgressBar.wrap(map.keySet().parallelStream(), "5-cross-10").forEach(symbol -> {
            List<CnStockData> stockDataList = map.get(symbol);
            BarSeries series = BarHelper.createBarSeries(symbol, stockDataList);
            Indicator<Num> closePrice = new ClosePriceIndicator(series);
            SMAIndicator smaShort = new SMAIndicator(closePrice, 5);
            SMAIndicator smaLong = new SMAIndicator(closePrice, 10);
            CrossedUpIndicatorRule rule = new CrossedUpIndicatorRule(smaShort, smaLong);
            ruleMap.put(symbol, rule);

            if (rule.isSatisfied(stockDataList.size() - 1)) {
                symbolList.add(symbol);
            }
        });
        return symbolList;
    }

    public Map<String, Object> rsiCompute(List<String> symbolList, int rsiDay) {
        LocalDate endDate = tradeDayRepository.findMaxDate().getDate();
        LocalDate startDate = endDate.minusDays(60);
        List<CnStockData> cnStockDataList = stockDataService.getRepository().getDataBetweenBySymbol(startDate, endDate, symbolList);
        Map<String, List<CnStockData>> map = cnStockDataList.stream().collect(Collectors.groupingBy(CnStockData::getSymbol));

        ConcurrentHashMap<String, Object> rsiMap = new ConcurrentHashMap<>();

        ProgressBar.wrap(map.keySet().parallelStream(), "RSI").forEach(symbol -> {
            List<CnStockData> stockDataList = map.get(symbol);
            BarSeries series = BarHelper.createBarSeries(symbol, stockDataList);
            Indicator<Num> closePrice = new ClosePriceIndicator(series);
            RSIIndicator rsiIndicator = new RSIIndicator(closePrice, rsiDay);
            Num value = rsiIndicator.getValue(stockDataList.size() - 1);
            if (value.doubleValue() > 50) {
                rsiMap.put(symbol, value);
            }
        });

        return rsiMap;
    }

}
