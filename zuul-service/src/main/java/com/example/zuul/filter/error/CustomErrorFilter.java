package com.example.zuul.filter.error;

import com.example.zuul.common.CustomFilter;
import com.example.zuul.common.ZuulConstant;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截所有 filter error 异常
 */
@Slf4j
//@Component
public class CustomErrorFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return CustomFilter.CustomFilterErrorFilter.getFilterType();
    }

    @Override
    public int filterOrder() {
        return CustomFilter.CustomFilterErrorFilter.getFilterOrder();
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();

        return null != ctx.get(ZuulConstant.ERROR_EXCEPTION) || null != ctx.getThrowable();
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        // 请求 url
        String requestURI = request.getRequestURI();
        // 微服务名
        String serviceId = (String) ctx.get(ZuulConstant.SERVICE_ID);

        Throwable throwable = ctx.getThrowable();
        if (null != throwable) {
            throwable = (Throwable) ctx.get(ZuulConstant.ERROR_EXCEPTION);
            if (throwable instanceof ZuulException) {
                ZuulException ex = (ZuulException) throwable;
                //设置状态码
                ctx.set(ZuulConstant.ERROR_STATUS_CODE, ex.nStatusCode);
                // 设置异常信息
                ctx.set(ZuulConstant.ERROR_MESSAGE, ex.errorCause);
                // 设置异常堆栈
                ctx.set(ZuulConstant.ERROR_EXCEPTION, ex);
            } else {
                //设置状态码
                ctx.set(ZuulConstant.ERROR_STATUS_CODE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                // 设置异常信息
                ctx.set(ZuulConstant.ERROR_MESSAGE, "网关内部异常");
                // 设置异常堆栈
                ctx.set(ZuulConstant.ERROR_EXCEPTION, null);
            }
        }

        // 获取抛出异常的信息
        log.error("网关异常:requestURI:{},serviceId:{}", requestURI, serviceId);

        return null;
    }
}
