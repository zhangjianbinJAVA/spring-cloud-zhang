package com.example.zuul.filter.post;

import com.example.zuul.common.CustomFilter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.CharEncoding;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * 设置状态码
 */
@Slf4j
@Component
public class CustomResponseCodeFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return CustomFilter.CustomResponseCodeFilter.getFilterType();
    }

    @Override
    public int filterOrder() {
        return CustomFilter.CustomResponseCodeFilter.getFilterOrder();
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        response.setCharacterEncoding(CharEncoding.UTF_8);

        log.info("统一设置响应状态码 200,实际响应状态码:{}", response.getStatus());
        if (response.getStatus() >= 500) {
            // 统一设置响应状态码 200
            ctx.setResponseStatusCode(200);
        }
        return null;
    }
}
