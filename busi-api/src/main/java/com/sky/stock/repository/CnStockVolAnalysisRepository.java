package com.sky.stock.repository;

import com.sky.stock.domian.entity.cn.CnStockVolAnalysis;
import com.sky.stock.domian.entity.cn.Id.UniqueId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CnStockVolAnalysisRepository extends JpaRepository<CnStockVolAnalysis, UniqueId> {

    List<CnStockVolAnalysis> findBySymbol(String symbol, Sort sort);

    Page<CnStockVolAnalysis> findByDateAndVolType(LocalDate date, Integer volType, Pageable pageable);
}