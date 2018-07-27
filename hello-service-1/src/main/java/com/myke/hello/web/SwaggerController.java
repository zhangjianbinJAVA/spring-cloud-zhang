package com.myke.hello.web;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwaggerController {


    @ApiOperation(value = "swagger 测试",
            extensions = @Extension(name = "x-zhang",
                    properties = @ExtensionProperty(name = "json", value = "keke")))
    @GetMapping(value = "/testSwagger")
    public String swagggerTest() {

        return "zhang";
    }


}
