package com.sky.stock.service;

import com.sky.stock.domian.entity.cn.CnStockInfo;
import com.sky.stock.helper.MathHelper;
import com.sky.stock.repository.CnStockInfoRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
@AllArgsConstructor
public class CnStockInfoService {

    private CnStockInfoRepository repository;

    @Cacheable(value = "CacheSymbol",key = "#symbol")
    public CnStockInfo findBySymbol(String symbol) {
        return repository.findBySymbol(symbol);
    }

    public List<CnStockInfo> list() {
        return repository.findAll();
    }

    @Cacheable(value = "MarketValuePeriod")
    public Map<Integer, List<CnStockInfo>> marketValuePeriod() {
        List<CnStockInfo> list = list();
        Map<Integer, List<CnStockInfo>> map = initMap();

        for (CnStockInfo cnStockInfo : list) {
            int i = MathHelper.marketValueToYi(cnStockInfo.getMarketValue());
            int period = 0;
            if (i <= 50) {
                period = 1;
            } else if (50 < i && i <= 100) {
                period = 2;
            } else if (100 < i && i <= 200) {
                period = 3;
            } else if (200 < i && i <= 500) {
                period = 4;
            } else if (500 < i && i <= 1000) {
                period = 5;
            } else if (1000 < i && i <= 5000) {
                period = 6;
            } else {
                period = 7;
            }
            map.get(period).add(cnStockInfo);
        }

        return map;
    }

    public Map<Integer, List<CnStockInfo>> initMap() {
        Map<Integer, List<CnStockInfo>> map = new HashMap<>();
        map.put(1, new ArrayList<>());
        map.put(2, new ArrayList<>());
        map.put(3, new ArrayList<>());
        map.put(4, new ArrayList<>());
        map.put(5, new ArrayList<>());
        map.put(6, new ArrayList<>());
        map.put(6, new ArrayList<>());
        map.put(7, new ArrayList<>());
        return map;
    }


}