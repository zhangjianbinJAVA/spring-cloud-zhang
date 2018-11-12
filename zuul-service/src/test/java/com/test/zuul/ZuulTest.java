package com.test.zuul;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.util.*;

/**
 * @author zhangjianbin
 * @version v1.0
 * @date 2017/7/26 16:22
 */
public class ZuulTest {

    @Test
    public void dateTest() {

        Map<String, Object> map = new LinkedHashMap();
        map.put("time", new Date());

        Object time = map.get("time");
        System.out.println(time);
    }

    @Test
    public void jsonTest() {
        JsonParser jsonParser = new JsonParser();
        JsonElement parse = jsonParser.parse("{\"1\":[\"key\",\"value\"]}");
        JsonObject jsonObject = parse.getAsJsonObject();
        JsonArray asJsonArray = jsonObject.get("1").getAsJsonArray();
        Iterator<JsonElement> iterator = asJsonArray.iterator();

        Map objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("var_k",iterator.next().toString().replace("\"",""));
    }

}
