package com.example.zuul.filter.pre;

import com.example.zuul.common.CustomFilter;
import com.example.zuul.util.RoutingUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 添加 请求头
 */
@Slf4j
@Component
public class CustomRequestHeaderFilter extends ZuulFilter {


    @Override
    public String filterType() {
        return CustomFilter.CustomRequestHeaderFilter.getFilterType();
    }

    @Override
    public int filterOrder() {
        return CustomFilter.CustomRequestHeaderFilter.getFilterOrder();
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Map<String, String> zuulRequestHeaders = ctx.getZuulRequestHeaders();

        // 获取获取当前域名
        String currentDomain = request.getServerName();
        currentDomain = StringUtils.removeStartIgnoreCase(currentDomain, "www.");
        // 添加自定定请求头
        ctx.addZuulRequestHeader("X-ECC-Zuul-Forwarded-Domain", currentDomain);

        log.info("网关请求头 zuulRequestHeaders:{}", zuulRequestHeaders);
        RoutingUtil.requestParamsPrint(request);
        return null;
    }

}
