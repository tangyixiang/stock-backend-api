package com.sky.stock.repository;

import com.sky.stock.domian.entity.cn.CnStockInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface CnStockInfoRepository extends JpaRepository<CnStockInfo, String>, JpaSpecificationExecutor<CnStockInfo> {

    CnStockInfo findBySymbol(String symbol);

    @Transactional
    @Modifying
    @Query("update cn_stock_info c set c.description = ?1 where c.symbol = ?2")
    void updateDescriptionBySymbol(String description, String symbol);


    @Transactional
    @Modifying
    @Query("update cn_stock_info c set c.name = ?1,c.marketValue=?2  where c.symbol = ?3")
    void updateDataByTask(String name, BigDecimal marketValue, String symbol);
}