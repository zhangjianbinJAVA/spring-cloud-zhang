package com.example.zuul.filter.pre;

import com.example.zuul.common.CustomFilter;
import com.example.zuul.common.ZuulConstant;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.util.HTTPRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 添加请求参数
 */
@Slf4j
@Component
public class CustomRequestParamFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return CustomFilter.CustomRequestParamFilter.getFilterType();
    }

    @Override
    public int filterOrder() {
        return CustomFilter.CustomRequestParamFilter.getFilterOrder();
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String zhang = request.getHeader("x-zhang");
        String bin = request.getHeader("x-bin");

        if (null == request.getQueryString()) {
            ctx.put("requestQueryParams", new HashMap<>());
        }

        // 添加请求参数 platformId
        String platformId = "2";

        if (StringUtils.isNotBlank(zhang)) {
            HTTPRequestUtils.getInstance().getQueryParams().put(ZuulConstant.PLATFORM_ID,
                    Arrays.asList(platformId));
        }

        if (StringUtils.isNotBlank(bin)) {
            HTTPRequestUtils.getInstance().getQueryParams().put(ZuulConstant.PLATFORM_ID,
                    Arrays.asList(platformId));
        }


        ctx.set(ZuulConstant.PLATFORM_ID, platformId);

        return null;
    }
}
