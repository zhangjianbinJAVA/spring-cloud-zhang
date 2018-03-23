package com.myke.hello.web;

import com.myke.common.MyKeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user: zhangjianbin <br/>
 * date: 2018/2/13 14:03
 */
@Import(MyKeConfig.class)
@RestController
public class MykeConfigController {

    @Autowired
    private MyKeConfig myKeConfig;

    @GetMapping("myke")
    public String properties() {
        return myKeConfig.getName();
    }
}
