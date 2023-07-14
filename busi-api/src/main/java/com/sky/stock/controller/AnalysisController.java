package com.sky.stock.controller;

import com.sky.stock.data.DataHandle;
import com.sky.stock.data.Request;
import com.sky.stock.data.RunningStatusData;
import com.sky.stock.domian.entity.cn.CnStockData;
import com.sky.stock.domian.entity.cn.CnStockIndustry;
import com.sky.stock.domian.entity.cn.CnStockInfo;
import com.sky.stock.domian.entity.cn.StockLHB;
import com.sky.stock.domian.model.CnStockIndustryDetail;
import com.sky.stock.domian.model.SymbolGains;
import com.sky.stock.helper.TradeDayHelper;
import com.sky.stock.repository.CnStockDataRepository;
import com.sky.stock.repository.CnStockIndustryRepository;
import com.sky.stock.repository.StockLHBRepository;
import com.sky.stock.service.CnStockDataService;
import com.sky.stock.service.CnStockInfoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.tablesaw.api.Table;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/cn/analysis")
@AllArgsConstructor
public class AnalysisController {

    private CnStockDataRepository stockDataRepository;

    private CnStockIndustryRepository stockIndustryRepository;

    private StockLHBRepository stockLHBRepository;

    private TradeDayHelper tradeDayHelper;

    private CnStockInfoService stockInfoService;

    private CnStockDataService stockDataService;

    @GetMapping("/increase/num")
    public Map<String, Integer> dailyIncreaseNum(String date) {
        List<CnStockData> list = stockDataRepository.findByDate(LocalDate.parse(date));
        int upNum = (int) list.stream().filter(cnStockData -> cnStockData.getDiffPer().doubleValue() >= 0).count();
        return Map.of("up", upNum, "down", list.size() - upNum);
    }

    @GetMapping("/market/up/down")
    public Map<String, Integer> getUpAndDownCount() {
        if (!RunningStatusData.getUpAndDownNum().isEmpty()) {
            return RunningStatusData.getUpAndDownNum();
        } else {
            LocalDate day = tradeDayHelper.getLatelyDay();
            List<CnStockData> list = stockDataRepository.findByDate(day);
            int upNum = (int) list.stream().filter(cnStockData -> cnStockData.getDiffPer().doubleValue() >= 0).count();
            return Map.of("up", upNum, "down", list.size() - upNum);
        }
    }

    @GetMapping("/industry/detail")
    public List<CnStockIndustryDetail> getIndustryDetail(String name) {
        Table table = Request.data("stock_board_industry_cons_em", Map.of("symbol", name));
        table.removeColumns("序号");
        List<Object[]> tableRow = DataHandle.getTableRow(table);
        List<String> fields = List.of("symbol", "name", "price", "diffPer", "diffQuota", "tradeVol", "tradeQuota", "amplitude", "high", "low", "open", "yesterdayClose", "exchangeRate", "pe", "pb");
        List<CnStockIndustryDetail> stockIndustryList = DataHandle.transferObject(CnStockIndustryDetail.class, fields, tableRow);
        return stockIndustryList;
    }

    @GetMapping("/history/industry")
    public List<CnStockIndustry> getHistoryIndustryRank(String date) {
        return stockIndustryRepository.findByDate(date, Sort.by("rank").ascending());
    }

    @GetMapping("/lhb")
    public List<StockLHB> getTodayLHB() {
        LocalDate day = tradeDayHelper.getLatelyDay();
        List<StockLHB> lhbList = stockLHBRepository.findByDate(day);
        return lhbList.stream().sorted(Comparator.comparing(StockLHB::getBuyingAmount).reversed()).toList();
    }

    @GetMapping("/market/value/distribution")
    public Map<Integer, Integer> marketValueDistribution() {
        Map<Integer, List<CnStockInfo>> map = stockInfoService.marketValuePeriod();
        LinkedHashMap<Integer, Integer> linkedHashMap = new LinkedHashMap<>();
        for (Integer code : map.keySet()) {
            linkedHashMap.put(code, map.get(code).size());
        }
        return linkedHashMap;
    }

    @GetMapping("/market/value/distribution/detail")
    public List<CnStockInfo> marketValueDistributionDetail(Integer type) {
        Map<Integer, List<CnStockInfo>> map = stockInfoService.marketValuePeriod();
        List<CnStockInfo> cnStockInfoList = map.get(type);
        cnStockInfoList = cnStockInfoList.stream().sorted(Comparator.comparing(CnStockInfo::getMarketValue).reversed()).toList();
        return cnStockInfoList;
    }

    @GetMapping("/gains/increase/list")
    public Set<SymbolGains> gainsIncreaseList(String type, int period) {
        return stockDataService.getGains(type, period, true);
    }

    @GetMapping("/gains/decrease/list")
    public Set<SymbolGains> gainsDecreaseList(String type, int period) {
        TreeSet<SymbolGains> symbolGainsTreeSet = stockDataService.getGains(type, period, false);
        return symbolGainsTreeSet.descendingSet();
    }
}
