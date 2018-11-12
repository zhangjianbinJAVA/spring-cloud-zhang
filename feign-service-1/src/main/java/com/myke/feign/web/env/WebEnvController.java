package com.myke.feign.web.env;

import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/10/26 14:54
 * ================================
 */
@RestController
public class WebEnvController {

    @Value("${author:no}")
    private String author;

    @Value("${city:no}")
    private String city;


    @GetMapping("/author")
    public String getAuthor() {
        return author;
    }

    @GetMapping("/city")
    public String getCity() {
        return city;
    }

}
