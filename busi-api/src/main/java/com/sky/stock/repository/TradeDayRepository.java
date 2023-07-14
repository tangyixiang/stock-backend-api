package com.sky.stock.repository;

import com.sky.stock.domian.entity.cn.TradeDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface TradeDayRepository extends JpaRepository<TradeDay, String> {

    TradeDay findByDate(LocalDate date);

    @Query(value = "select max(date) as date from trade_day", nativeQuery = true)
    TradeDay findMaxDate();

    @Query(value = "select date from trade_day order by date desc offset ?1 limit 1 ", nativeQuery = true)
    TradeDay periodDate(int offset);

    @Query(value = "select date from trade_day where date >= ?1 order by date asc limit 1",nativeQuery = true)
    TradeDay firstDateInYear(LocalDate date);
}