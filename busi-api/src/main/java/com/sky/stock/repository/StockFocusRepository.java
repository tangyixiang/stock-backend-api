package com.sky.stock.repository;

import com.sky.stock.domian.entity.cn.StockFocus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockFocusRepository extends JpaRepository<StockFocus, String> {
    List<StockFocus> findBySymbol(String symbol, Sort sort);

    @Modifying
    @Query(value = "select symbol from stock_focus group by symbol",nativeQuery = true)
    List<String> findAllSymbol();


}