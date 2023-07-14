package com.sky.stock;

import com.sky.stock.service.UsStockFutuService;
import com.sky.stock.task.UsStockDataTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class StockBackendApiApplicationTests {

    @Autowired
    UsStockFutuService futuService;
    @Autowired
    UsStockDataTask task;

    @Test
    void run2() {
        task.syncSymbol();
    }


}
