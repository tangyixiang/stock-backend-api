package com.sky.stock.controller;

import com.sky.stock.domian.entity.cn.StockFocus;
import com.sky.stock.domian.vo.StockFocusVo;
import com.sky.stock.repository.StockFocusRepository;
import com.sky.stock.service.CnStockDataService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cn")
@AllArgsConstructor
public class StockFocusController {

    private StockFocusRepository repository;

    private CnStockDataService dataService;

    @PostMapping("/focus/add")
    public void addFocus(@RequestBody StockFocus stockFocus) {
        stockFocus.setDate(LocalDate.now());
        repository.save(stockFocus);
    }

    @GetMapping("/focus/list")
    public List<StockFocusVo> findAll() {
        List<StockFocusVo> voList = new ArrayList<>();
        List<String> allSymbol = repository.findAllSymbol();
        for (String symbol : allSymbol) {
            List<StockFocus> list = repository.findBySymbol(symbol, Sort.by("date").ascending());
            StockFocus first = list.get(0);
            StockFocusVo vo = new StockFocusVo();
            vo.setSymbol(symbol);
            vo.setFirstFocusDate(first.getDate());
            //vo.setDiffPerOfYear(dataService.getGains(symbol, "y", 1));
            if (list.size() > 1) {
                vo.setLastFocusDate(list.get(list.size() - 1).getDate());
            }
            voList.add(vo);
        }
        return voList;
    }

}
