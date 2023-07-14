package com.sky.stock.repository;

import com.sky.stock.domian.entity.cn.CnStockIndustry;
import com.sky.stock.domian.entity.cn.Id.UniqueIndustryId;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CnStockIndustryRepository extends JpaRepository<CnStockIndustry, UniqueIndustryId> {

    List<CnStockIndustry> findByDate(String date, Sort sort);
}