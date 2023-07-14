package com.sky.stock.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.sky.stock.data.DataHandle;
import com.sky.stock.domian.entity.cn.CnStockInfo;
import com.sky.stock.domian.model.RequestModel;
import com.sky.stock.service.CnStockDataService;
import com.sky.stock.service.CnStockInfoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/cn/init")
public class StockInitController {

    private CnStockInfoService stockInfoService;

    private CnStockDataService stockDataService;

    @GetMapping("/stock/data")
    public String initStockData() {
        List<CnStockInfo> stockInfoList = stockInfoService.list();
        String today = DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        RequestModel.StockModel stockDataModel = new RequestModel.StockModel("000001", "20150101", today, "daily", "qfq");
        stockInfoList.parallelStream().forEach(stock -> DataHandle.ignoreException(() -> stockDataService.saveStockData(stockDataModel.withSymbol(stock.getSymbol())), stock.getSymbol() + "数据同步异常"));
        return "success";
    }
}
