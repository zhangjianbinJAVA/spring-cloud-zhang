package com.example.zuul.filter.pre;

import com.example.zuul.common.CustomFilter;
import com.example.zuul.common.ZuulConstant;
import com.example.zuul.exception.ecc.CustomZuulException;
import com.example.zuul.feign.HelloFeignClient;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 网关调用远程服务 helloFeignClient
 * <p>
 * 1. 当没有 feign-service-1 服务时的异常
 * 2. 当 访问 feign-service-1 服务时访问出现异常时
 */
@Slf4j
@Component
public class CustomHelloFeignClientFilter extends ZuulFilter {

    @Autowired
    private HelloFeignClient helloFeignClient;

    @Override
    public String filterType() {
        return CustomFilter.CustomHelloFeignClientFilter.getFilterType();
    }

    @Override
    public int filterOrder() {
        return CustomFilter.CustomHelloFeignClientFilter.getFilterOrder();
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String exception = request.getParameter("feign");
        return StringUtils.isNotBlank(exception);
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String exception = request.getParameter("feign");


        if ("123".equals(exception)) {
            try {
                // 测试访问 feign 服务端抛出出现异常时,异常信息存入全局变量中
                String result = helloFeignClient.throwError(exception);
                log.info("hello feign error result:{}", result);
            } catch (Exception e) {
                ctx.put(ZuulConstant.ERROR_EXCEPTION, e);
            }
        } else if ("read-time-out".equals(exception)) {
            try {
                // 测试访问 feign 出现超时时,异常信息存入全局变量中
                String resultTimeout = helloFeignClient.timeOut();
                log.info("hello feign read-time-out result:{}", resultTimeout);
            } catch (Exception e) {
                ctx.put(ZuulConstant.ERROR_EXCEPTION, e);
            }
        } else if ("custom-error".equals(exception)) {
            try {
                // 测试访问 feign 服务端抛出出现异常时, 异常信息 再次封装抛出
                String result = helloFeignClient.throwError(exception);
                log.info("hello feign custom-error result:{}", result);
            } catch (Exception e) {
                throw new CustomZuulException("1000-10", "调用 feign-service-1 服务失败");
            }

        } else if ("0".equals(exception)) {
            // 测试 feign 访问时，出现异常时不进行拦截
            String result = helloFeignClient.throwError(exception);
            log.info("hello feign 出现异常时不进行拦截 result:{}", result);
        } else if ("10".equals(exception)) {
            // feigｎ　正测情况下
            String result = helloFeignClient.zuulTest();
            log.info("hello feign  result:{}", result);
        }

        return null;
    }
}

