package com.sky.stock.controller;

import com.sky.stock.domian.model.PageResult;
import com.sky.stock.domian.param.PageQuery;
import com.sky.stock.domian.vo.PankouChangeVo;
import com.sky.stock.service.PankouChangeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cn/analysis")
@AllArgsConstructor
public class PankouChangeController {

    private PankouChangeService pankouChangeService;

    @GetMapping("/pankou/change")
    public PageResult pankou(PageQuery query) {
        return pankouChangeService.getPankouByDate(query);
    }

    @GetMapping("/pankou/change/counts")
    public List<PankouChangeVo> counts(LocalDate date) {
        return pankouChangeService.counts(date);
    }
}
