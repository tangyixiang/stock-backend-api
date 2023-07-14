package com.sky.stock.service;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.sky.stock.domian.entity.us.UsStockData;
import com.sky.stock.domian.entity.us.UsStockInfo;
import com.sky.stock.domian.model.FutuStockData;
import com.sky.stock.domian.model.FutuUsData;
import com.sky.stock.helper.DateHelper;
import com.sky.stock.helper.JsonHelper;
import com.sky.stock.helper.MathHelper;
import com.sky.stock.helper.RateLimiterHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@AllArgsConstructor
public class UsStockFutuService {

    private ObjectMapper objectMapper;

    private UsStockInfoService stockInfoService;

    private UsStockDataService stockDataService;

    public void syncSymbol() {
        log.info("US -> 开始同步symbol");
        FutuUsData index = getStockList(0);
        Integer totalPage = index.getData().getPagination().getPageCount();
        Collection<FutuUsData.StockInfo> stockInfoSet = new HashSet<>();
        RateLimiterHelper.run("us-symbol", 4.0, totalPage, i -> stockInfoSet.addAll(getStockList(i).getData().getList()));
        stockInfoService.saveOrUpdate(stockInfoSet);
        log.info("US -> 同步symbol完成");
    }

    public String getCompanyInfo(Long stockId) {
        String url = "https://www.moomoo.com/quote-api/quote-v2/get-company-info?";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("stock_id", String.valueOf(stockId));
        String data = request(url, params);
        log.info("公司返回:{}", data);
        try {
            Map<String, Object> map = JsonHelper.jsonPathData(data, "/data");
            ArrayList companyProfile = (ArrayList) map.get("companyProfile");
            for (Object o : companyProfile) {
                Map<?, ?> profile = (Map<?, ?>) o;
                String name = String.valueOf(profile.get("name"));
                if (name.equals("公司简介")) {
                    return (String) profile.get("value");
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public List<UsStockData> initCompanyData(Long stockId) {
        UsStockInfo usStockInfo = stockInfoService.getRepository().findById(stockId).get();
        log.info("开始初始化数据:{}", usStockInfo.getSymbol());
        CookieStore cookieStore = new BasicCookieStore();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

        String initUrl = "https://www.futunn.com/stock/HON-US?seo_redirect=1&channel=1244&subchannel=2&from=BaiduAladdin&utm_source=alading_user&utm_medium=website_growth";
        HttpGet initRequest = new HttpGet(initUrl);
        String token = "";
        String html = "";
        try {
            html = EntityUtils.toString(httpClient.execute(initRequest, context).getEntity());
            Document document = Jsoup.parse(html);
            Element metaElement = document.selectFirst("meta");
            token = metaElement.attr("content");

        } catch (Exception e) {
            log.error("html获取失败,返回值:{}", html);
            throw new RuntimeException("初始化获取cookie失败");
        }


        String url = "https://www.futunn.com/quote-api/get-kline?";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("stock_id", stockId + "");
        params.put("type", "2");
        params.put("market_type", "2");
        String data = requestDomestic(httpClient, context, url, params, token);
        Map<String, Object> map = JsonHelper.jsonPathData(data, "/data");
        String jsonStr = JsonHelper.serialize(map.get("list"));
        List<FutuStockData> futuStockDataList = JsonHelper.deserializeList(jsonStr, new TypeReference<List<FutuStockData>>() {
        });
        List<UsStockData> dataList = new ArrayList<>();
        for (FutuStockData futuStockData : futuStockDataList) {
            LocalDate date = DateHelper.usDate(futuStockData.getK() * 1000);
            if (date.isBefore(LocalDate.of(2015, 1, 1)) || date.isAfter(LocalDate.now())) {
                continue;
            }
            UsStockData usStockData = new UsStockData();
            usStockData.setStockId(stockId);
            usStockData.setSymbol(usStockInfo.getSymbol());
            futuDataCovertEntity(futuStockData, usStockData);
            dataList.add(usStockData);
        }
        log.info("初始化数据结束:{}", usStockInfo.getSymbol());
        return dataList;
    }

    public void syncLatestData(Long stockId) {
        String url = "https://www.moomoo.com/quote-api/quote-v2/get-stock-quote?";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("stockId", String.valueOf(stockId));
        params.put("marketType", "2");
        params.put("marketCode", "11");
        params.put("lotSize", "1");
        params.put("spreadCode", "45");
        params.put("underlyingStockId", "0");
        params.put("instrumentType", "3");
        String data = request(url, params);
        Map<String, Object> map = JsonHelper.jsonPathData(data, "/data");
        LocalDate date = DateHelper.usDate(Long.parseLong(map.get("time") + ""));
        String close = (String) map.get("priceNominal");
        String open = (String) map.get("priceOpen");
        String high = (String) map.get("priceHighest");
        String low = (String) map.get("priceLowest");
        Double tradeVol = MathHelper.strToNumber((String) map.get("volume"));
        Double tradeQuota = MathHelper.strToNumber((String) map.get("turnover"));
        Double marketValue = MathHelper.strToNumber((String) map.get("totalMarketCap"));
        String diffPer = (String) map.get("changeRatio");
        String diffQuota = (String) map.get("change");
        String exchangeRate = (String) map.get("ratioTurnover");
        String amplitude = (String) map.get("amplitudePrice");

        UsStockInfo usStockInfo = stockInfoService.getRepository().findById(stockId).get();
        usStockInfo.setMarketValue(new BigDecimal(marketValue));
        stockInfoService.getRepository().save(usStockInfo);

        UsStockData usStockData = new UsStockData();
        usStockData.setStockId(stockId);
        usStockData.setSymbol(usStockInfo.getSymbol());
        usStockData.setDate(date);
        usStockData.setOpen(new BigDecimal(open));
        usStockData.setClose(new BigDecimal(close));
        usStockData.setHigh(new BigDecimal(high));
        usStockData.setLow(new BigDecimal(low));
        usStockData.setTradeVol(new BigDecimal(tradeVol));
        usStockData.setTradeQuota(new BigDecimal(tradeQuota));
        usStockData.setAmplitude(new BigDecimal(amplitude.replace("%", "")));
        usStockData.setDiffPer(new BigDecimal(diffPer.replace("%", "")));
        usStockData.setDiffQuota(new BigDecimal(diffQuota));
        usStockData.setExchangeRate(new BigDecimal(exchangeRate.replace("%", "")));
        log.info("US -> 保存:{} 数据", usStockInfo.getSymbol());
        stockDataService.getRepository().save(usStockData);
    }

    public void futuDataCovertEntity(FutuStockData futuStockData, UsStockData usStockData) {
        usStockData.setDate(DateHelper.usDate(futuStockData.getK() * 1000));
        usStockData.setOpen(MathHelper.round(Double.parseDouble(futuStockData.getO())));
        usStockData.setClose(MathHelper.round(Double.parseDouble(futuStockData.getC())));
        usStockData.setHigh(MathHelper.round(Double.parseDouble(futuStockData.getH())));
        usStockData.setLow(MathHelper.round(Double.parseDouble(futuStockData.getL())));
        usStockData.setTradeVol(MathHelper.round(futuStockData.getV()));
        usStockData.setTradeQuota(MathHelper.round(futuStockData.getT()));
        // 振幅,昨天close == 0,数据有问题
        if (futuStockData.getLc() != 0.0) {
            BigDecimal amplitude = usStockData.getHigh().subtract(usStockData.getLow()).divide(new BigDecimal(futuStockData.getLc()), 8, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
            usStockData.setAmplitude(amplitude);
            double diffPer = MathHelper.round((Double.parseDouble(futuStockData.getC()) - futuStockData.getLc()) / futuStockData.getLc() * 100, 2);
            usStockData.setDiffPer(MathHelper.round(diffPer));
        } else {
            // 都为0
            usStockData.setAmplitude(new BigDecimal(0));
            usStockData.setDiffPer(new BigDecimal(0));
        }
        usStockData.setDiffQuota(MathHelper.round(Double.parseDouble(futuStockData.getCp())));
        usStockData.setExchangeRate(MathHelper.round(futuStockData.getT() * 100));
    }

    private FutuUsData getStockList(Integer page) {
        String url = "https://www.moomoo.com/quote-api/quote-v2/get-stock-list?";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("marketType", "2");
        params.put("plateType", "1");
        params.put("rankType", "3");
        params.put("page", page + "");
        params.put("pageSize", "100");

        FutuUsData futuUsData = JsonHelper.deserialize(request(url, params), FutuUsData.class);
        return futuUsData;
    }

    private String request(String url, Map<String, String> params) {
        HttpResponse<String> response = null;
        try {
            String paramStr = getQueryParamStr(params);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + paramStr)).GET().header("Quote-Token", getSign(params))
                    .header("Referer", "https://www.moomoo.com/hans/quote/us/stock-list/all-us-stocks/top-market-cap").build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            log.error("返回数据:{},错误信息:{}", response.body(), e);
        }
        return "";
    }

    private String requestDomestic(CloseableHttpClient httpClient, HttpClientContext context, String url, Map<String, String> params, String token) {
        try {
            String paramStr = getQueryParamStr(params);
            HttpGet httpGet = new HttpGet(url + paramStr);
            httpGet.setHeader("futu-x-csrf-token", token);
            CloseableHttpResponse response = httpClient.execute(httpGet, context);
            String responseBody = EntityUtils.toString(response.getEntity());
            return responseBody;
        } catch (Exception e) {
            log.error("返回数据:{}", e);
        }
        return "";
    }

    private String getQueryParamStr(Map<String, String> params) {
        return Joiner.on("&").withKeyValueSeparator("=").join(params);
    }

    private String getSign(Map<String, String> params) {
        try {
            String body = objectMapper.writeValueAsString(params);
            String hmacSHA512 = hmacSHA512("quote_web", body);
            return DigestUtil.sha256Hex(hmacSHA512.substring(0, 10)).substring(0, 10);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String hmacSHA512(String key, String message) {
        HMac hMac = new HMac(HmacAlgorithm.HmacSHA512, key.getBytes());
        return hMac.digestHex(message.getBytes());
    }

}
