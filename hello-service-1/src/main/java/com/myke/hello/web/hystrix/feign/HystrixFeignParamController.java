package com.myke.hello.web.hystrix.feign;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HystrixFeignParamController {


    @RequestMapping(value = "/request-param", method = RequestMethod.GET)
    public Map<String, String> requestParam(@RequestParam String name, @RequestParam String address) {
        HashMap<String, String> result = new HashMap<>();
        result.put("name", name);
        result.put("address", address);
        return result;
    }

    @RequestMapping(value = "/request-pathvariable/{id}", method = RequestMethod.GET)
    public Map<String, String> requestPathvariable(@PathVariable Integer id) {
        HashMap<String, String> result = new HashMap<>();
        result.put("id", id.toString());
        return result;
    }

    @RequestMapping(value = "/request-map", method = RequestMethod.GET)
    public Map<String, String> requestMap(@RequestParam String name, @RequestParam String address) {
        HashMap<String, String> result = new HashMap<>();
        result.put("name", name);
        result.put("address", address);
        return result;
    }


    @RequestMapping(value = "/request-header", method = RequestMethod.GET)
    public Map<String, String> requestHead(@RequestHeader String name, @RequestHeader Integer age) {
        HashMap<String, String> result = new HashMap<>();
        result.put("name", name);
        result.put("age", age.toString());
        return result;
    }

    @RequestMapping(value = "/request-body", method = RequestMethod.POST)
    public Map<String, String> requestBody(@RequestBody Map map) {
        return map;
    }

}
