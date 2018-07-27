package com.example.zuul.filter.post;

import com.example.zuul.common.ZuulConstant;
import com.example.zuul.exception.ecc.CustomZuulException;
import com.google.gson.Gson;
import com.netflix.client.ClientException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import static com.example.zuul.common.ZuulConstant.ERROR_STATUS_CODE;


/**
 * 拦截所有 filter error 异常
 */
@Slf4j
@Component
public class CustomSendErrorFilter extends ZuulFilter {

    public static final String DEFAULT_ERR_MSG = "系统繁忙,请稍后再试";

    @Autowired
    private Gson gson;

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.containsKey(ERROR_STATUS_CODE)
                || null != ctx.get(ZuulConstant.ERROR_EXCEPTION)
                || null != ctx.getThrowable();
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        //网关路由的服务名
        Object serviceIdObject = ctx.get(ZuulConstant.SERVICE_ID);
        String serviceId = null;
        if (null != serviceIdObject) {
            serviceId = (String) serviceIdObject;
        }

        String traceId = ctx.getZuulRequestHeaders().get(ZuulConstant.REQUEST_TRAND_ID);

        Throwable error = null;

        try {
            String detailMessage = (String) ctx.get("error.message");
            String message = DEFAULT_ERR_MSG;
            // zuul 异常变量
            boolean isError = ctx.containsKey("error.exception");

            if (!isError) {
                isError = ctx.getThrowable() != null ? true : false;
            } else {
                error = (Exception) ctx.get("error.exception");
            }

            if (isError) {
                if (error == null) {
                    error = ctx.getThrowable();
                }
                Throwable re = getOriginException(error);
                detailMessage = re.getMessage();
                log.warn("uri:{},error:{}", request.getRequestURI(), re.getMessage());
                if (re instanceof ConnectException) {
                    message = "网关路由 " + serviceId + " 服务连接超时";
                } else if (re instanceof SocketTimeoutException || re instanceof TimeoutException) {
                    message = "网关路由 " + serviceId + " 服务读取超时";
                } else if (re instanceof ClientException) {
                    message = "网关路由 " + serviceId + " 服务失败";
                } else if (re instanceof FeignException) {
                    message = "网关路由 " + serviceId + " 服务失败";
                } else if (re instanceof ZuulException) {
                    message = "网关内部异常";
                } else if (re instanceof CustomZuulException) {
                    CustomZuulException exception = (CustomZuulException) re;
                    message = exception.getMessage();
                } else {
                    log.warn("网关处理未知的异常", error);
                }
            }

            if (StringUtils.isBlank(detailMessage)) {
                detailMessage = DEFAULT_ERR_MSG;
            }

            //异常处理结果
            errorResponse(ctx, gson.toJson(new CustomZuulException("9000-10", message, detailMessage, request.getRequestURI(), traceId, serviceId)));
        } catch (Exception e) {
            log.error("网关处理异常 error", e);
            errorResponse(ctx, gson.toJson(new CustomZuulException("9000-11", "网关处理异常 error", e.getMessage(), request.getRequestURI(), traceId, serviceId)));
        }
        return null;
    }

    private Throwable getOriginException(Throwable e) {
        Throwable error = e;
        e = e.getCause();
        if (e == null) {
            return error;
        }
        while (e.getCause() != null) {
            e = e.getCause();
        }
        return e;
    }


    /**
     * 返回网关异常信息
     *
     * @param ctx
     * @param errorInfo
     */
    private void errorResponse(RequestContext ctx, String errorInfo) {
        ctx.setSendZuulResponse(true);
        ctx.setResponseStatusCode(200);
        ctx.setResponseBody(errorInfo);
        ctx.getResponse().setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
    }
}
