package com.myke.feign.client;

import com.jd.ecc.commons.web.model.RespData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/7/19
 * Time： 14:02
 * ================================
 */
@FeignClient("authority-service")
public interface AuthClient {

    @RequestMapping("/feign/resource/queryAllResources?platformId=40")
    RespData queryAllResources();
}
