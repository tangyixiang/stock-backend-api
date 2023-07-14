package com.sky.stock.data;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Source;
import tech.tablesaw.io.json.JsonReadOptions;
import tech.tablesaw.io.json.JsonReader;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Request {

    public static HttpResponse stockApi(String api, Map<String, Object> params) {
        String path = SpringUtil.getProperty("stock.path");
        return HttpRequest.get(path + api).form(params).execute();
    }

    public static Table data(String api) {
        return data(api, null, null);
    }

    public static Table data(String api, Map<String, Object> params) {
        return data(api, params, null);
    }

    public static Table dataSpecifiedColum(String api, Map<String, ColumnType> columnTypeByName) {
        return data(api, null, columnTypeByName);
    }

    //@Retryable(retryFor = {RequestException.class}, backoff = @Backoff(delay = 5000))
    public static Table data(String api, Map<String, Object> params, Map<String, ColumnType> columnTypeByName) {
        HttpResponse httpResponse = stockApi(api, params);
        log.debug("api请求返回值:{}", httpResponse.body());
        if (httpResponse.getStatus() != 200) {
            log.error("请求api异常,api:{},参数:{}", api, params);
            return null;
        } else {
            JsonReader jsonReader = new JsonReader();
            Table table = jsonReader.read(JsonReadOptions.builder(Source.fromString(httpResponse.body()))
                    .columnTypesPartial(options(columnTypeByName))
                    .build());
            return table;
        }
    }

    public static Map<String, ColumnType> options(Map<String, ColumnType> columnTypeByName) {
        Map<String, ColumnType> initType = new HashMap<>();
        initType.put("代码", ColumnType.STRING);
        if (columnTypeByName != null)
            initType.putAll(columnTypeByName);
        return initType;
    }


}
