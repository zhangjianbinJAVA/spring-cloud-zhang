package com.myke.hello.web;

import com.myke.common.MyKeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * user: zhangjianbin <br/>
 * date: 2018/2/13 14:03
 */
@Import(MyKeConfig.class) //导入配置类
@RestController
public class MykeConfigController {

    @Autowired
    private MyKeConfig myKeConfig; //注入并使用配置类

    @GetMapping("myke")
    public String properties(@RequestParam(required = false) Long platformId) {
        return myKeConfig.getName();
    }
}
