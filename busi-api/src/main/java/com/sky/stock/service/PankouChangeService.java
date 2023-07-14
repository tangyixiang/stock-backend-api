package com.sky.stock.service;

import com.sky.stock.domian.entity.cn.CnStockInfo;
import com.sky.stock.domian.entity.cn.PankouChange;
import com.sky.stock.domian.model.PageResult;
import com.sky.stock.domian.param.PageQuery;
import com.sky.stock.domian.vo.PankouChangeVo;
import com.sky.stock.repository.PankouChangeRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Service
@AllArgsConstructor
public class PankouChangeService {

    private PankouChangeRepository repository;

    private CnStockDataService dataService;

    private CnStockInfoService stockInfoService;

    public PageResult getPankouByDate(PageQuery query) {
        List<PankouChange> changeList = repository.findByTimeBetween(LocalDateTime.of(query.getDate(), LocalTime.MIN), LocalDateTime.of(query.getDate(), LocalTime.MAX));
        Map<String, List<PankouChange>> collect = changeList.stream().collect(Collectors.groupingBy(PankouChange::getSymbol));
        List<PankouChangeVo> voList = new ArrayList<>();

        collect.keySet().stream().skip(query.getPageSize() * (query.getPageNo() - 1)).limit(query.getPageSize()).forEach(symbol -> {
            PankouChangeVo vo = new PankouChangeVo();
            vo.setSymbol(symbol);
            CnStockInfo stockInfo = stockInfoService.findBySymbol(symbol);
            vo.setName(stockInfo.getName());
            vo.setMarketValue(stockInfo.getMarketValue());
            vo.setCount(collect.get(symbol).size());
            vo.setDetail(collect.get(symbol));
            vo.setData(dataService.findFixedData(symbol, 120));
            voList.add(vo);
        });
        return PageResult.of(collect.size(), voList);
    }

    public List<PankouChangeVo> counts(LocalDate date) {
        List<PankouChange> changeList = repository.findByTimeBetween(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
        Map<String, List<PankouChange>> collect = changeList.stream().collect(Collectors.groupingBy(PankouChange::getSymbol));
        List<PankouChangeVo> voList = new ArrayList<>();

        for (String symbol : collect.keySet()) {
            PankouChangeVo vo = new PankouChangeVo();
            vo.setSymbol(symbol);
            CnStockInfo stockInfo = stockInfoService.findBySymbol(symbol);
            if (stockInfo == null) {
                continue;
            }
            vo.setName(stockInfo.getName());
            vo.setMarketValue(stockInfo.getMarketValue());
            vo.setCount(collect.get(symbol).size());
            vo.setDetail(collect.get(symbol));
            voList.add(vo);
        }

        List<PankouChangeVo> list = voList.stream().sorted(Comparator.comparing(PankouChangeVo::getCount).reversed()).toList();
        return list;
    }
}
