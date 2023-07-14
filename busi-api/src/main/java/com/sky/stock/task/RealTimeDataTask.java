package com.sky.stock.task;

import com.sky.stock.data.RunningStatusData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RealTimeDataTask {

    @Scheduled(cron = "0 20 23 ? * MON-FRI")
    public void clearData() {
        RunningStatusData.clear();
    }
}
