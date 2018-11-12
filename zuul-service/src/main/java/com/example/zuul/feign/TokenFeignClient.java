package com.example.zuul.feign;

import com.example.zuul.dto.TokenDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/8/10
 * Time： 16:27
 * ================================
 */
@FeignClient(name = "oauth2-server", url = "http://127.0.0.1:8790")
public interface TokenFeignClient {

    @RequestMapping(value = "/oauth/check_token", method = RequestMethod.POST)
    TokenDTO check_token(@RequestParam("token") String token);
}
