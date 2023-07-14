package com.sky.stock.controller;

import com.sky.stock.data.DataHandle;
import com.sky.stock.domian.entity.cn.CnStockInfo;
import com.sky.stock.domian.entity.cn.CnStockVolAnalysis;
import com.sky.stock.domian.param.VolAnalysisQuery;
import com.sky.stock.domian.vo.VolAnalysisVo;
import com.sky.stock.repository.CnStockVolAnalysisRepository;
import com.sky.stock.service.CnStockDataService;
import com.sky.stock.service.CnStockInfoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/cn")
public class CnStockVolAnalysisController {

    private CnStockVolAnalysisRepository repository;

    private CnStockDataService dataService;

    private CnStockInfoService cnStockInfoService;

    @GetMapping("/analysis/vol")
    public VolAnalysisVo volAnalysis(VolAnalysisQuery query) {
        PageRequest pageRequest = DataHandle.convertPage(query, Sort.by("tradeVolPct").descending());
        Page<CnStockVolAnalysis> volAnalysisPage = repository.findByDateAndVolType(query.getDate(), query.getVolType(), pageRequest);
        List<CnStockVolAnalysis> content = volAnalysisPage.getContent();

        List<VolAnalysisVo.AnalysisData> list = content.parallelStream().map(vol -> {
            VolAnalysisVo.AnalysisData analysisData = new VolAnalysisVo.AnalysisData();
            CnStockInfo cnStockInfo = cnStockInfoService.findBySymbol(vol.getSymbol());
            analysisData.setSymbol(vol.getSymbol());
            analysisData.setName(cnStockInfo.getName());
            analysisData.setTradeVolPct(vol.getTradeVolPct());
            analysisData.setMarketValue(cnStockInfo.getMarketValue());
            analysisData.setData(dataService.findFixedData(vol.getSymbol(), 120));
            return analysisData;
        }).toList();

        VolAnalysisVo vo = new VolAnalysisVo();
        vo.setTotal(volAnalysisPage.getTotalElements());
        vo.setList(list);
        return vo;
    }

    @GetMapping("/vol/single/analysis")
    public List<CnStockVolAnalysis> findBySymbol(String symbol) {
        return repository.findBySymbol(symbol, Sort.by("date").ascending());
    }
}
