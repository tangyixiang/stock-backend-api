package com.sky.stock.service;

import com.sky.stock.repository.UsStockDataRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
@AllArgsConstructor
public class UsStockDataService {

    private UsStockDataRepository repository;


}