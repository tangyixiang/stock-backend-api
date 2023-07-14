package com.sky.stock.data;

import com.sky.stock.domian.model.OpenVolUp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 运行中的辅助数据
 */
public class RunningStatusData {

    private static HashMap<String, OpenVolUp> todayVolMap = new HashMap<>();

    private static HashMap<String, Integer> upAndDownNum = new HashMap();

    public static void addVolUp(OpenVolUp openVolUp) {
        todayVolMap.put(openVolUp.getSymbol(), openVolUp);
    }

    public static void setUpAndDownNum(Map<String, Integer> map) {
        upAndDownNum.putAll(map);
    }

    public static Collection<OpenVolUp> getTodayVolUp() {
        return todayVolMap.values();
    }

    public static Map<String, Integer> getUpAndDownNum() {
        return upAndDownNum;
    }

    public static void clear(){
        todayVolMap.clear();
    }

}
