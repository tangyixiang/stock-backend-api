package com.sky.stock.controller;

import com.sky.stock.service.TechAnalysisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/tech/analysis")
public class TechAnalysisController {

    private TechAnalysisService techAnalysisService;

    @GetMapping("/breakthrough/5days")
    public List<String> breakthrough() {
        return techAnalysisService.fiveDaysBreakthrough();
    }


}