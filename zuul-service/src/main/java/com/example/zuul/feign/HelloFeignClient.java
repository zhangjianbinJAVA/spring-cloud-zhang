package com.example.zuul.feign;


import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("feign-service-1")
@RequestMapping("/custom")
public interface HelloFeignClient {

    @RequestMapping(value = "/zuul/error", method = RequestMethod.GET)
    String throwError(@RequestParam(value = "exception", required = false) String exception);

    @RequestMapping(value = "/zuul/timeout", method = RequestMethod.GET)
    String timeOut();

    @RequestMapping(value = "/zuul/test", method = RequestMethod.GET)
    String zuulTest();

}
