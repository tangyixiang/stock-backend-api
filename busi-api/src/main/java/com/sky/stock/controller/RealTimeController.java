package com.sky.stock.controller;

import com.sky.stock.data.DataHandle;
import com.sky.stock.data.Request;
import com.sky.stock.data.RunningStatusData;
import com.sky.stock.domian.entity.cn.PankouChange;
import com.sky.stock.domian.entity.cn.CnStockIndustry;
import com.sky.stock.domian.model.OpenVolUp;
import com.sky.stock.domian.vo.PankouChangeVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.tablesaw.api.Table;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/realtime/cn")
public class RealTimeController {

    @GetMapping("/industry")
    public List<CnStockIndustry> getRealTimeIndustryRank() {
        Table table = Request.data("stock_board_industry_name_em");
        List<Object[]> tableRow = DataHandle.getTableRow(table);
        List<String> fields = List.of("rank", "industryName", "industryCode", "price", "diffQuota", "diffPer", "marketValue", "exchangeRate", "upNum", "downNum", "leaderName", "leaderPer");
        List<CnStockIndustry> stockIndustryList = DataHandle.transferObject(CnStockIndustry.class, fields, tableRow);
        stockIndustryList.sort(Comparator.comparing(CnStockIndustry::getRank));
        return stockIndustryList;
    }

    @GetMapping("/vol/up")
    public Collection<OpenVolUp> getVolUp() {
        Collection<OpenVolUp> todayVolUp = RunningStatusData.getTodayVolUp();
        List<OpenVolUp> list = todayVolUp.stream().sorted(Comparator.comparing(OpenVolUp::getRiseRatio).reversed()).toList();
        return list;
    }

    @GetMapping("/pankou/change")
    public List<PankouChangeVo> pankou(String type) {
        Table table = Request.data("stock_changes_em", Map.of("symbol", type));
        List<String> fields = List.of("time", "symbol", "name", "type", "message");
        List<Object[]> tableRow = DataHandle.getTableRow(table);
        List<PankouChange> pankouList = DataHandle.transferObject(PankouChange.class, fields, tableRow);
        Map<String, List<PankouChange>> collect = pankouList.stream().collect(Collectors.groupingBy(PankouChange::getSymbol));
        List<PankouChangeVo> voList = new ArrayList<>();
        for (String symbol : collect.keySet()) {
            PankouChangeVo vo = new PankouChangeVo();
            vo.setSymbol(symbol);
            vo.setCount(collect.get(symbol).size());
            vo.setDetail(collect.get(symbol));
            voList.add(vo);
        }
        return voList;
    }
}
