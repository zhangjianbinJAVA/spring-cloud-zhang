package com.example.zuul.filter.route;

import com.example.zuul.common.CustomFilter;
import com.netflix.zuul.ZuulFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomRoutingFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return CustomFilter.CustomRoutingFilter.getFilterType();
    }

    @Override
    public int filterOrder() {
        return CustomFilter.CustomRoutingFilter.getFilterOrder();
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        return null;
    }


}
