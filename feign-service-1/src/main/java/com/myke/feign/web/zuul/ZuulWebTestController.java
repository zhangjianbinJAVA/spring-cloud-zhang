package com.myke.feign.web.zuul;

import com.myke.feign.client.HelloFeignClient;
import com.myke.feign.exception.ecc.BaseException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/custom")
public class ZuulWebTestController {

    @Autowired
    private HelloFeignClient helloFeignClient;

    @GetMapping("/zuul/error")
    public String throwError(@RequestParam(required = false) String exception) {
        if (StringUtils.isNotBlank(exception)) {
            throw new BaseException("20-001", "exception 参数异常");
        }
        return "测试 网关 api throw error"  ;
    }

    @GetMapping("/zuul/timeout")
    public String timeOut() {
        try {
            Thread.sleep(6000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "测试 网关 api timeout ";
    }

    @GetMapping("/zuul/test")
    public String zuulTest() {
        return "zuul test 正常";
    }

    @GetMapping("/zuul/feign/request-param")
    public Map<String, String> requestParam(@RequestParam(required = false) String name, @RequestParam(required = false) String address) {
        return helloFeignClient.requestParam(name, address);
    }

    @GetMapping("/zuul/feign/request-error")
    public Map<String, String> requestError(@RequestParam(required = false) String name, @RequestParam(required = false) String address) {
        return helloFeignClient.requestError();
    }

}
