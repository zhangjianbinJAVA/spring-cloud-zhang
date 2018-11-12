package com.myke.feign.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/10/30 16:50
 * ================================
 */
@FeignClient(name = "cache", url = "http://gw.buy5j.com/cache")
public interface CacheClient {

    @RequestMapping(value = "/v2/save2Cache", method = RequestMethod.POST)
    boolean save2Cache(
            @RequestParam("platformId") Long platformId,
            @RequestParam("key") String key,
            @RequestBody String value,
            @RequestParam("expireSecond") Integer expireSecond);

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    String get(
            @RequestParam("platformId") Long platformId,
            @RequestParam("key") String key);
}
