package com.myke.hello.web.hystrix.feign;

import com.myke.hello.exception.ecc.BaseException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HystrixFeignErrorController {

    @RequestMapping(value = "/feign/request-error", method = RequestMethod.GET)
    Map<String, String> requestError() {
        throw new BaseException("200-3", "feign 调用异常");
    }
}
