package com.myke.feign.config.env;

import org.springframework.core.env.EnumerablePropertySource;

import java.util.Map;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/10/26 14:47
 * ================================
 */
public class MyPropertySource extends EnumerablePropertySource<Map<String, String>> {

    public MyPropertySource(String name, Map<String, String> source) {
        super(name, source);
    }

    //获取所有的配置名字
    @Override
    public String[] getPropertyNames() {
        return source.keySet().toArray(new String[source.size()]);
    }

    //根据配置返回对应的属性
    @Override
    public Object getProperty(String name) {
        return source.get(name);
    }
}
