package com.test.zuul;

import org.junit.Test;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

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

}
