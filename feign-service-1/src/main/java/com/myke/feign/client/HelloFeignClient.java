package com.myke.feign.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 14:33
 */
//@FeignClient(value = "HELLO-SERVICE-1",configuration = DisableHystrixConfiguration.class)
@FeignClient(value = "HELLO-SERVICE-1")
//@RequestMapping(value = "/abc") // 这个起作用，可以用下面url的前缀
public interface HelloFeignClient {

    /******************** 参数 ***************************/

    @RequestMapping(value = "/request-param", method = RequestMethod.GET)
    Map<String, String> requestParam(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "address", required = false) String address);


    @RequestMapping(value = "/request-header", method = RequestMethod.GET)
    Map<String, String> requestHead(@RequestHeader("name") String name, @RequestHeader("age") Integer age);

    @RequestMapping(value = "/request-body", method = RequestMethod.POST)
    Map<String, String> requestBody(@RequestBody Map map);


    @RequestMapping(value = "/request-pathvariable/{id}", method = RequestMethod.GET)
    Map<String, String> requestPathvariable(@PathVariable("id") Integer id);

    @RequestMapping(value = "/request-map", method = RequestMethod.GET)
    Map<String, String> requestMap(@RequestParam Map<String, String> map);


    /***********************************************/

    @RequestMapping(value = "/api/timeout", method = RequestMethod.GET)
    Map<String, String> timeout();

    /**********************************************/

    @RequestMapping(value = "/feign/request-error", method = RequestMethod.GET)
    Map<String, String> requestError();

    /**
     * 测试 hello-1
     *
     * @return
     */
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    String getHello();


}



