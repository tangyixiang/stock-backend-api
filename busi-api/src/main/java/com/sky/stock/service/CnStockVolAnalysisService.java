package com.sky.stock.service;

import com.sky.stock.data.RunningStatusData;
import com.sky.stock.domian.entity.cn.CnStockData;
import com.sky.stock.domian.entity.cn.CnStockInfo;
import com.sky.stock.domian.entity.cn.StockDailyData;
import com.sky.stock.domian.model.OpenVolUp;
import com.sky.stock.helper.MathHelper;
import com.sky.stock.repository.CnStockVolAnalysisRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Service
@AllArgsConstructor
public class CnStockVolAnalysisService {

    private CnStockVolAnalysisRepository repository;

    private CnStockDataService stockDataService;

    private CnStockInfoService stockInfoService;

    public void compareLastDayVol(List<StockDailyData> stockDailyData) {
        Map<String, CnStockData> map = stockDataService.lastDayData();
        HashMap<String, StockDailyData> currentDataMap = new HashMap<>();
        stockDailyData.forEach(dailyData -> currentDataMap.put(dailyData.getSymbol(), dailyData));
        currentDataMap.forEach((k, v) -> {
            CnStockData cnStockData = map.get(k);
            if (cnStockData != null) {
                int tradeVol = cnStockData.getTradeVol().intValue();
                if (v.getTradeVol() > tradeVol) {
                    CnStockInfo cnStockInfo = stockInfoService.findBySymbol(k);
                    // 开始超过昨天的量
                    RunningStatusData.addVolUp(new OpenVolUp(k, MathHelper.round((double) v.getTradeVol() / tradeVol - 1, 2),
                            v.getDiffPer(), v.getPrice(),cnStockInfo.getMarketValue(),cnStockData.getTradeQuota()));
                }
            }
        });
    }

}