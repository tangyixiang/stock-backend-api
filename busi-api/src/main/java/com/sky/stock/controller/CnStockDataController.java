package com.sky.stock.controller;

import com.sky.stock.domian.entity.cn.CnStockData;
import com.sky.stock.domian.entity.cn.CnStockInfo;
import com.sky.stock.domian.vo.VolAnalysisVo;
import com.sky.stock.service.CnStockDataService;
import com.sky.stock.service.CnStockInfoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@RestController
@RequestMapping("/cn")
@AllArgsConstructor
public class CnStockDataController {

    private CnStockDataService dataService;

    private CnStockInfoService stockInfoService;

    @GetMapping("/symbol/history/data")
    public List<CnStockData> historyData(String symbol, Integer period) {
        return dataService.findFixedData(symbol, period == null ? 300 : period);
    }

    /**
     * 查找多个symbol
     */
    @GetMapping("/collection/symbol/data")
    public Collection<VolAnalysisVo.AnalysisData> getSymbolListData(String symbolStr, Integer period) {
        String[] symbolArray = symbolStr.split(",");
        ConcurrentLinkedDeque<VolAnalysisVo.AnalysisData> dataList = new ConcurrentLinkedDeque<>();
        Arrays.stream(symbolArray).parallel().forEach(symbol -> {
            VolAnalysisVo.AnalysisData data = new VolAnalysisVo.AnalysisData();
            data.setSymbol(symbol);
            CnStockInfo cnStockInfo = stockInfoService.findBySymbol(symbol);
            data.setName(cnStockInfo.getName());
            data.setMarketValue(cnStockInfo.getMarketValue());
            data.setData(historyData(symbol, period == null ? 120 : period));
            dataList.add(data);
        });

        return dataList.stream().sorted(Comparator.comparing(VolAnalysisVo.AnalysisData::getMarketValue)).toList();
    }
}
