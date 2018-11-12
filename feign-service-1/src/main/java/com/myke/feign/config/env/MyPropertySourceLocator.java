package com.myke.feign.config.env;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * 该方案的思路来源于 Spring Cloud Config。
 *
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/10/26 14:58
 * ================================
 */
public class MyPropertySourceLocator implements PropertySourceLocator {


    @Override
    public PropertySource<?> locate(Environment environment) {
        System.out.println("run MyPropertySourceLocator");

        //属性存于 bootstrapProperties

        //简单起见，这里直接创建一个map,你可以在这里写从哪里获取配置信息。这里应该从远程配置中心拉取，实际中间件开发时替换为配置中心的client
        Map<String, String> properties = new HashMap<>();
        properties.put("city", "henan");

        //spring.cloud.config.overrideNone为true的逻辑，
        // 会将该属性降低为最低优先级（放到数组的最末尾），
        // 这样就实现了服务端修改来达到本地配置覆盖远程配置的功能。
        // PropertySourceBootstrapConfiguration
        properties.put("spring.cloud.config.overrideNone","true");

        MyPropertySource myPropertySource = new MyPropertySource("myPropertySource", properties);
        return myPropertySource;
    }
}
