package com.sky.stock.data;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.sky.stock.domian.entity.cn.CnStockData;
import com.sky.stock.domian.param.PageQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Source;
import tech.tablesaw.io.json.JsonReadOptions;
import tech.tablesaw.io.json.JsonReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DataHandle {

    public static List<Object[]> getTableRow(Table table) {
        int row = table.rowCount();
        int columns = table.columns().size();

        List<Object[]> list = new ArrayList<>(row);

        for (int i = 0; i < row; i++) {
            Row dataRow = table.row(i);
            Object[] data = new Object[columns];
            for (int j = 0; j < columns; j++) {
                data[j] = dataRow.getObject(j);
            }
            list.add(data);
        }
        return list;
    }

    public static <T> List<T> transferObject(Class<T> cls, List<String> fields, List<Object[]> values) {
        try {
            ArrayList<T> list = new ArrayList<>();
            for (Object[] value : values) {
                T t = cls.getDeclaredConstructor().newInstance();
                HashMap<String, Object> map = new HashMap<>();
                for (int i = 0; i < fields.size(); i++) {
                    map.put(fields.get(i), value[i]);
                }
                BeanUtil.fillBeanWithMap(map, t, true);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void ignoreException(Runnable runnable, String error) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("运行异常:{}", error);
        }
    }

    public static List<double[]> ohlc(List<CnStockData> dataList) {
        double[] open = dataList.stream().mapToDouble(data -> data.getOpen().doubleValue()).toArray();
        double[] high = dataList.stream().mapToDouble(data -> data.getHigh().doubleValue()).toArray();
        double[] low = dataList.stream().mapToDouble(data -> data.getLow().doubleValue()).toArray();
        double[] close = dataList.stream().mapToDouble(data -> data.getClose().doubleValue()).toArray();
        return List.of(open, high, low, close);
    }


    public static Table toTable(Object o, Map<String, ColumnType> columnTypeByName) {
        JsonReader jsonReader = new JsonReader();
        HashMap<String, ColumnType> map = new HashMap<>();
        if (columnTypeByName != null) {
            map.putAll(columnTypeByName);
        }
        map.putAll(Map.of("date", ColumnType.LOCAL_DATE, "symbol", ColumnType.STRING));
        Table table = jsonReader.read(JsonReadOptions.builder(Source.fromString(JSON.toJSONString(o)))
                .columnTypesPartial(map)
                .build());
        return table;
    }

    public static PageRequest convertPage(PageQuery pageQuery, Sort sort) {
        return PageRequest.of(pageQuery.getPageNo() - 1, pageQuery.getPageSize(), sort);
    }

}
