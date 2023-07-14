package com.sky.stock.repository;

import com.sky.stock.domian.entity.cn.CnStockData;
import com.sky.stock.domian.entity.cn.Id.UniqueId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface CnStockDataRepository extends JpaRepository<CnStockData, UniqueId> {

    @Query(value = "select * from cn_stock_data where symbol =:symbol and date > :date order by date asc limit 1", nativeQuery = true)
    CnStockData findBySymbolLeDate(@Param("symbol") String symbol, @Param("date") LocalDate date);

    List<CnStockData> findByDate(LocalDate date);

    @Query(value = "select new com.sky.stock.domian.entity.cn.CnStockData(u.symbol,u.date,u.open,u.close,u.high,u.low) from cn_stock_data u where u.date =:date")
    List<CnStockData> findOHLCByDate(@Param("date") LocalDate date);

    long removeBySymbolIn(Collection<String> symbols);

    @Query(value = "select * from (select * from cn_stock_data where symbol = :symbol order by date desc limit :limit) t order by date asc", nativeQuery = true)
    List<CnStockData> findFixedData(@Param("symbol") String symbol, @Param("limit") int limit);

    @Query(value = "select max(date) from cn_stock_data", nativeQuery = true)
    String findMaxDate();

    @Query(value = "select * from cn_stock_data where date >= ?1 and date <= ?2 order by date asc", nativeQuery = true)
    List<CnStockData> getDataBetween(LocalDate startDate, LocalDate endDate);

    @Query(value = "select * from cn_stock_data where date >= ?1 and date <= ?2 and symbol in (?3) order by date asc", nativeQuery = true)
    List<CnStockData> getDataBetweenBySymbol(LocalDate startDate, LocalDate endDate, List<String> symbol);
}