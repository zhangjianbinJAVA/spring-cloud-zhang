package com.myke.feign.config.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.bind.PropertySourcesPropertyValues;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 允许定制应用的上下文的应用环境优于应用的上下文之前被刷新。
 * <p>
 * （意思就是在spring上下文构建之前可以设置一些系统配置）
 * <p>
 * 必须要在META-INF/spring.factories文件中去注册
 */
public class MyEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "defaultProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.out.println("run  MyEnvironmentPostProcessor");
        Properties properties = new Properties();
        //这里应该从远程配置中心拉取，实际中间件开发时替换为配置中心的client

        // 不覆盖属性
        properties.put("author", "keke");
        PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource("my", properties);
        environment.getPropertySources().addLast(propertiesPropertySource);

    }

    private void addOrReplace(MutablePropertySources propertySources,
                              Map<String, Object> map) {
        MapPropertySource target = null;
        if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
            PropertySource<?> source = propertySources.get(PROPERTY_SOURCE_NAME);
            if (source instanceof MapPropertySource) {
                target = (MapPropertySource) source;
                for (String key : map.keySet()) {
                    if (!target.containsProperty(key)) {
                        target.getSource().put(key, map.get(key));
                    }
                }
            }
        }
        if (target == null) {
            target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
        }
        if (!propertySources.contains(PROPERTY_SOURCE_NAME)) {
            propertySources.addLast(target);
        }
    }
}
