package com.sky.stock.repository;

import com.sky.stock.domian.entity.cn.Id.UniqueId;
import com.sky.stock.domian.entity.cn.StockLHB;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockLHBRepository extends JpaRepository<StockLHB, UniqueId> {

    List<StockLHB> findByDate(LocalDate date);
}