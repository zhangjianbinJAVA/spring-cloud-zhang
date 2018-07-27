package com.example.zuul.filter.route;

import com.example.zuul.common.CustomFilter;
import com.example.zuul.util.RoutingUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.example.zuul.util.RoutingUtil.filterThrowError;

/**
 * pre 异常
 */
@Slf4j
@Component
public class CustomThrowErrorRouteFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return CustomFilter.CustomThrowErrorRouteFilter.getFilterType();
    }

    @Override
    public int filterOrder() {
        return CustomFilter.CustomThrowErrorRouteFilter.getFilterOrder();
    }

    @Override
    public boolean shouldFilter() {
        return RoutingUtil.requestParamError(RequestContext.getCurrentContext());
    }

    @Override
    public Object run() {
        // 通过 zuul 全局变量存放异常
        filterThrowError();
        return null;
    }
}
