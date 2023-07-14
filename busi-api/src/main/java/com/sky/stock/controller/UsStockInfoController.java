package com.sky.stock.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.sky.stock.data.DataHandle;
import com.sky.stock.domian.entity.us.UsStockData;
import com.sky.stock.domian.records.StockRecord;
import com.sky.stock.service.UsStockDataService;
import com.sky.stock.service.UsStockFutuService;
import com.sky.stock.service.UsStockInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequestMapping("/us/")
@AllArgsConstructor
public class UsStockInfoController {

    private UsStockInfoService stockInfoService;

    private UsStockFutuService futuService;

    private UsStockDataService usStockDataService;


    @GetMapping("/futu/symbol")
    public String futuData() {
        futuService.syncSymbol();
        return "ok";
    }

    @GetMapping("/init/futu/data")
    public String initFutuData() {
        RateLimiter rateLimiter = RateLimiter.create(4.0);
        List<StockRecord> stockRecordList = stockInfoService.getRepository().findAllSymbol();
        ProgressBar.wrap(IntStream.range(0, stockRecordList.size()).parallel(), "us-stock-data").forEach(i -> {
            rateLimiter.acquire();
            StockRecord data = stockRecordList.get(i);
            DataHandle.ignoreException(() -> {
                List<UsStockData> dataList = futuService.initCompanyData(data.getStockId());
                usStockDataService.getRepository().saveAll(dataList);
            }, "异常运行" + data.getStockId());
        });
        return "ok";
    }

}
