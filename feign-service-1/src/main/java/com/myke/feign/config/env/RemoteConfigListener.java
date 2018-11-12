package com.myke.feign.config.env;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/10/26 16:11
 * ================================
 * <p>
 * <p>
 * 实现  ApplicationEvent   会调用很多
 * <p>
 * 实现  ApplicationEnvironmentPreparedEvent  只会在上下文启动之前调用一次
 * Enviroment已经准备完毕，但此时上下文context还没有创建。
 */
@Slf4j
public class RemoteConfigListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final String APPLICATION_PROPERTIES = "properties";

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {

        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            System.out.println("run RemoteConfigListener " + event.toString());

            ConfigurableEnvironment env = event.getEnvironment();


            Map<String, String> properties = new HashMap<>();
            try {

                //Properties props = new Properties();
                //这是伪代码，value是从配置中心拿到的配置
                //props.load(new StringReader(value));
                //props.load(new StringReader("k=z"));

                properties.put("city", "henan2");
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("load properties finished...........");
            log.info("properties文件内容：" + properties);
            env.getPropertySources().addLast(new MyPropertySource(APPLICATION_PROPERTIES, properties));
        }

    }
}
