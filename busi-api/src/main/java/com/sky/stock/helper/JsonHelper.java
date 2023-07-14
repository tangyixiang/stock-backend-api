package com.sky.stock.helper;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JsonHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("序列化json异常:{}", e);
            return "";
        }
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("反序列化json异常:{}", e);
            return null;
        }
    }

    public static <T> T deserializeList(String json, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(json, valueTypeRef);
        } catch (JsonProcessingException e) {
            log.error("反序列化json异常:{}", e);
            return null;
        }
    }

    public static Map<String, Object> toMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据 JSON 路径获取数据并返回 Map 类型
     *
     * @param json  JSON 字符串
     * @param path  JSON 路径
     * @return 包含指定路径下数据的 Map 对象
     */
    public static Map<String, Object> jsonPathData(String json, String path) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode dataNode = rootNode.at(JsonPointer.compile(path));

            return objectMapper.convertValue(dataNode, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
