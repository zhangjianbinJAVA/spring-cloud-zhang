package com.myke.feign.exception.ecc.feign;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myke.feign.exception.ecc.ServiceClientException;
import com.myke.feign.exception.ecc.ServiceServerException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangzhonghua on 2017/1/11.
 */
@Component
public class EccErrorDecoder implements ErrorDecoder {

    @Autowired
    private Gson gson;


    final ErrorDecoder defaultDecoder = new Default();

    private Logger LOG = LoggerFactory.getLogger(EccErrorDecoder.class);


    @Override
    public Exception decode(String methodKey, Response response) {

        // 字符串转为map
        Map<String, Object> map = null;

        if (null != response.body() && !("".equals(response.body()))) {
            try {
                Type type = new TypeToken<Map<String, String>>() {
                }.getType();

                // http响应转化为字符串
                map = gson.fromJson(Util.toString(response.body().asReader()), type);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (response.status() == 406) {
            map = new HashMap();
            map.put("message", "远程调用客户端不接收所响应的MIME类型");
        } else {
            map = new HashMap();
            map.put("message", String.format("远程调用服务端http响应码[%s]", response.status()));
        }
        //远程调用出错的方法
        map.put("remoteMethod", methodKey);

        if (response.status() >= 500 && response.status() <= 599) {
            // 服务端抛出的异常信息
            return new ServiceServerException(map);

        } else if (response.status() >= 400 && response.status() <= 499) {
            return new ServiceClientException(map);
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
