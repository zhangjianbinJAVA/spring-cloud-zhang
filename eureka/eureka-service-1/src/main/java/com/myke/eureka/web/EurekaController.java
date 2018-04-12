package com.myke.eureka.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EurekaController {

    @GetMapping("/ex")
    public String page() {
        return "zhang";
    }
}
