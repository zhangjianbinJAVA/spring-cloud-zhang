package com.myke.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * user: zhangjianbin <br/>
 * date: 2018/2/13 11:44
 */


@ConfigurationProperties("my.ke")
public class MyKeConfig {

    @Setter
    @Getter
    private String name;

}
