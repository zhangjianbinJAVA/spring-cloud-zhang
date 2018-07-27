package com.myke.feign.web;

import com.jd.ecc.commons.web.model.RespData;
import com.myke.feign.client.AuthClient;
import com.myke.feign.client.HelloFeignClient;
import com.myke.feign.client.HelloFeignClient2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 14:35
 */
@RestController
public class HelloFeignController {

    @Autowired
    private HelloFeignClient helloFeignClient;

    @Autowired
    private HelloFeignClient2 helloFeignClient2;

    @Autowired
    private AuthClient authClient;

    @RequestMapping(value = "/hello-1", method = RequestMethod.GET)
    public String hello1() {
        return helloFeignClient.getHello();
    }

    @RequestMapping(value = "/hello-2", method = RequestMethod.GET)
    public String hello2() {
        return helloFeignClient2.getHello();
    }

    @RequestMapping(value = "/request-param", method = RequestMethod.GET)
    public Map<String, String> requestParam(@RequestParam(required = false) String name, @RequestParam(required = false) String address) {
        return helloFeignClient.requestParam(name, address);
    }


    @RequestMapping(value = "/request-header", method = RequestMethod.GET)
    public Map<String, String> requestHead() {
        return helloFeignClient.requestHead("zhang", 90);
    }

    @RequestMapping(value = "/request-body", method = RequestMethod.POST)
    public Map<String, String> requestBody() {
        HashMap<Object, Object> result = new HashMap<>();
        result.put("body", "1234");
        return helloFeignClient.requestBody(result);
    }

    @RequestMapping(value = "/request-pathvariable/{id}", method = RequestMethod.GET)
    public Map<String, String> requestPathvariable() {
        return helloFeignClient.requestPathvariable(23);
    }

    @RequestMapping(value = "/request-map", method = RequestMethod.GET)
    public Map<String, String> requestMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("name", "zhang");
        result.put("address", "beijing");
        return helloFeignClient.requestMap(result);
    }


    /***********************************************************/

    @RequestMapping(value = "/api/timeout", method = RequestMethod.GET)
    public Map<String, String> timeout() {
        return helloFeignClient.timeout();
    }


    @GetMapping("/queryAllResources")
    public RespData qurery() {
        return authClient.queryAllResources();
    }
}
