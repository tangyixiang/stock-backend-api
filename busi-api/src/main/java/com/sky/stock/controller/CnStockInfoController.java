package com.sky.stock.controller;

import com.sky.stock.base.GenericSpecification;
import com.sky.stock.base.SearchCriteria;
import com.sky.stock.data.DataHandle;
import com.sky.stock.domian.entity.cn.CnStockInfo;
import com.sky.stock.domian.model.PageResult;
import com.sky.stock.domian.param.StockInfoQuery;
import com.sky.stock.enums.SearchOperation;
import com.sky.stock.repository.CnStockInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cn")
@AllArgsConstructor
public class CnStockInfoController {

    private CnStockInfoRepository repository;

    @RequestMapping("/symbol/list")
    public PageResult listSymbol(StockInfoQuery query) {
        GenericSpecification specification = new GenericSpecification<CnStockInfo>();
        specification.add(new SearchCriteria("market_value", query.getMax(), SearchOperation.LESS_THAN_EQUAL));
        specification.add(new SearchCriteria("market_value", query.getMin(), SearchOperation.GREATER_THAN_EQUAL));
        specification.add(new SearchCriteria("symbol", query.getSymbol(), SearchOperation.EQUAL));

        PageRequest pageRequest = DataHandle.convertPage(query, Sort.by("marketValue").descending());
        Page page = repository.findAll(specification, pageRequest);
        return PageResult.of(page.getTotalElements(), page.getContent());
    }

    @GetMapping("/symbol/info")
    public CnStockInfo getInfo(String symbol) {
        return repository.findBySymbol(symbol);
    }


}
