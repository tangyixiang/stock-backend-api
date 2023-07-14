package com.sky.stock.service;

import com.sky.stock.domian.entity.us.UsStockInfo;
import com.sky.stock.domian.model.FutuUsData;
import com.sky.stock.helper.MathHelper;
import com.sky.stock.repository.UsStockInfoRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Slf4j
@Getter
@Service
@AllArgsConstructor
public class UsStockInfoService {

    private UsStockInfoRepository repository;

    public void saveOrUpdate(Collection<FutuUsData.StockInfo> list) {
        for (FutuUsData.StockInfo stockInfo : list) {
            UsStockInfo usStockInfo = new UsStockInfo();
            usStockInfo.setStockId(stockInfo.getStockId());
            usStockInfo.setSymbol(stockInfo.getSymbol());
            usStockInfo.setName(stockInfo.getName());
            usStockInfo.setMarketLabel(stockInfo.getMarketLabel());
            Double value = MathHelper.strToNumber(stockInfo.getMarketVal());
            usStockInfo.setMarketValue(new BigDecimal(value));
            if (value >= 20d * 10000 * 10000) {
                repository.save(usStockInfo);
            }
        }
    }

    public List<UsStockInfo> getNotDescSymbol() {
        List<UsStockInfo> all = repository.findAll();
        List<UsStockInfo> list = all.stream().filter(info -> StringUtils.isEmpty(info.getDescription())).toList();
        return list;
    }

}
