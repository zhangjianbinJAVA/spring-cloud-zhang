package com.example.zuul.filter.pre;

import com.example.zuul.common.CustomFilter;
import com.example.zuul.common.ZuulConstant;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 记录 网关开始执行时间
 */
@Slf4j
@Component
public class CustomZuulStartTimeFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return CustomFilter.CustomZuulStartTimeFilter.getFilterType();
    }

    @Override
    public int filterOrder() {
        return CustomFilter.CustomZuulStartTimeFilter.getFilterOrder();
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.set(ZuulConstant.SERVICE_START_TIME, System.currentTimeMillis());
        return null;
    }
}
