package com.example.zuul.filter.post;

import com.example.zuul.common.CustomFilter;
import com.example.zuul.util.RoutingUtil;
import com.netflix.zuul.ZuulFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomZuulFilterDebugFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return CustomFilter.CustomZuulFilterDebugFilter.getFilterType();
    }

    @Override
    public int filterOrder() {
        return CustomFilter.CustomZuulFilterDebugFilter.getFilterOrder();
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RoutingUtil.zuulDebugPrint();
        return null;
    }
}
