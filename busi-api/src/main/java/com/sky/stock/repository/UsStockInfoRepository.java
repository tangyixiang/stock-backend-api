package com.sky.stock.repository;

import com.sky.stock.domian.entity.us.UsStockInfo;
import com.sky.stock.domian.records.StockRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UsStockInfoRepository extends JpaRepository<UsStockInfo, Long>, JpaSpecificationExecutor<UsStockInfo> {

    @Transactional
    @Modifying
    @Query("update us_stock_info u set u.description = ?1 where u.stockId = ?2")
    void updateDescriptionByStockId(String description, Long stockId);

    @Query("select new com.sky.stock.domian.records.StockRecord(u.stockId,u.symbol) from us_stock_info u where u.description = ''  ")
    List<StockRecord> findSymbolEmptyDesc();

    @Query("select new com.sky.stock.domian.records.StockRecord(u.stockId,u.symbol) from us_stock_info u")
    List<StockRecord> findAllSymbol();

}