package com.sky.stock.service;

import com.sky.stock.repository.CnStockIndustryRepository;
import lombok.Getter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@AllArgsConstructor
public class CnStockIndustryService {

    private CnStockIndustryRepository repository;
}