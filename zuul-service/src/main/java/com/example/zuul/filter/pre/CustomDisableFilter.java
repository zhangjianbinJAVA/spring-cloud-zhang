package com.example.zuul.filter.pre;

import com.example.zuul.common.CustomFilter;
import com.example.zuul.util.RoutingUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 测试 禁用 filter 继续向下执行
 */
@Slf4j
@Component
public class CustomDisableFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return CustomFilter.CustomDisableFilter.getFilterType();
    }

    @Override
    public int filterOrder() {
        return CustomFilter.CustomDisableFilter.getFilterOrder();
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return null != ctx.getRequest().getParameter("disable-filter");
    }

    @Override
    public Object run() {
        RoutingUtil.disableZuulFilter();
        return null;
    }
}
