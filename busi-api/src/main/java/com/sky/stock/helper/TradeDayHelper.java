package com.sky.stock.helper;

import com.sky.stock.domian.entity.cn.TradeDay;
import com.sky.stock.repository.TradeDayRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@AllArgsConstructor
public class TradeDayHelper {

    private TradeDayRepository repository;

    public boolean todayIsTrade() {
        TradeDay date = repository.findByDate(LocalDate.now());
        if (date == null) {
            log.info("今日不是交易日");
        }
        return date != null;
    }

    public LocalDate getLatelyDay() {
        TradeDay maxDate = repository.findMaxDate();
        return maxDate.getDate();
    }

}
