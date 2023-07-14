package com.sky.stock.repository;

import com.sky.stock.domian.entity.cn.CnStockTickData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CnStockTickDataRepository extends JpaRepository<CnStockTickData, Long> {
}