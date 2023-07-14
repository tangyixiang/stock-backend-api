package com.sky.stock.task;

import com.sky.stock.data.DataHandle;
import com.sky.stock.domian.entity.us.UsStockInfo;
import com.sky.stock.domian.records.StockRecord;
import com.sky.stock.helper.RateLimiterHelper;
import com.sky.stock.service.UsStockFutuService;
import com.sky.stock.service.UsStockInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(name = "task.us-enable", havingValue = "true")
public class UsStockDataTask {

    private UsStockInfoService stockInfoService;

    private UsStockFutuService futuService;

    @Scheduled(cron = "0 20 04 ? * TUE-SAT")
    public void syncSymbol() {
        futuService.syncSymbol();
    }

    @Scheduled(cron = "0 30 04 ? * TUE-SAT")
    public void usCompanyInfo() {
        List<UsStockInfo> stockInfoList = stockInfoService.getNotDescSymbol();
        RateLimiterHelper.run("us-company-desc", 6.0, stockInfoList.size(), i -> DataHandle.ignoreException(() -> {
            String companyInfo = futuService.getCompanyInfo(stockInfoList.get(i).getStockId());
            if (companyInfo != null) {
                stockInfoService.getRepository().updateDescriptionByStockId(companyInfo, stockInfoList.get(i).getStockId());
            }
        }, "信息获取异常:" + stockInfoList.get(i).getSymbol()));
    }

    @Scheduled(cron = "0 10 05 ? * TUE-SAT")
    public void openDayData() {
        log.info("US -> 开始同步最新的数据");
        List<StockRecord> allSymbol = stockInfoService.getRepository().findAllSymbol();
        RateLimiterHelper.run("us-latest-data", 4.0, allSymbol.size(), i ->
                DataHandle.ignoreException(() -> futuService.syncLatestData(allSymbol.get(i).getStockId()), "同步最新数据异常:" + allSymbol.get(i).getSymbol())
        );

    }
}
