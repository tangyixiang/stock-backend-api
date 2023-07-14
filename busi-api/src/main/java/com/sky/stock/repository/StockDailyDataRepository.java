package com.sky.stock.repository;

import com.sky.stock.domian.entity.cn.Id.UniqueTimeId;
import com.sky.stock.domian.entity.cn.StockDailyData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDailyDataRepository extends JpaRepository<StockDailyData, UniqueTimeId> {
}