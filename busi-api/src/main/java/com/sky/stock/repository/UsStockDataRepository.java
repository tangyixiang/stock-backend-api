package com.sky.stock.repository;

import com.sky.stock.domian.entity.us.Id.FutuUniqueId;
import com.sky.stock.domian.entity.us.UsStockData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsStockDataRepository extends JpaRepository<UsStockData, FutuUniqueId> {
}