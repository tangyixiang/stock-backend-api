package com.sky.stock.repository;

import com.sky.stock.domian.entity.cn.PankouChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PankouChangeRepository extends JpaRepository<PankouChange, Long> {

    List<PankouChange> findByTimeBetween(LocalDateTime timeStart, LocalDateTime timeEnd);

    @Query(value = "select distinct symbol from pan_kou_change where time >= ?1 and time <= ?2 ", nativeQuery = true)
    List<String> findSymbolByDate(LocalDateTime startTime, LocalDateTime endTime);
}