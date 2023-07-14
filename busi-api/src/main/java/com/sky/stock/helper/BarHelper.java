package com.sky.stock.helper;

import com.sky.stock.domian.entity.cn.CnStockData;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;

import java.time.*;
import java.util.List;

public class BarHelper {

    public static BarSeries createBarSeries(String symbol, List<CnStockData> list) {
        BarSeries series = new BaseBarSeriesBuilder().withName(symbol).build();
        for (CnStockData cnStockData : list) {
            series.addBar(getCnTime(cnStockData.getDate()), cnStockData.getOpen(), cnStockData.getHigh(), cnStockData.getLow(), cnStockData.getClose());
        }
        return series;
    }

    private static ZonedDateTime getCnTime(LocalDate date) {
        // 获取时区
        ZoneId zoneId = ZoneId.of("Asia/Shanghai"); // 以亚洲/上海时区为例
        // 将本地日期与本地时间合并为本地日期时间
        LocalDateTime localDateTime = LocalDateTime.of(date, LocalTime.of(15, 00));

        // 使用时区创建ZonedDateTime对象
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        return zonedDateTime;
    }
}
