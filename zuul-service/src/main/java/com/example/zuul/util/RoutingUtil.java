package com.example.zuul.util;

import com.example.zuul.common.ZuulConstant;
import com.google.gson.Gson;
import com.netflix.zuul.context.Debug;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.util.HTTPRequestUtils;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.ribbon.RibbonHttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.Kernel;
import java.util.*;

/**
 * @author zhangjianbin
 * @version v1.0
 * @date 2017/7/26 20:24
 */
@Slf4j
public class RoutingUtil {
    private static final Gson gson = new Gson();

    /**
     * 记录 zuul 路由 debug 信息
     *
     * @param ctx
     * @param debugInfo
     */
    public static void addRoutingDebug(RequestContext ctx, String debugInfo) {
        boolean bDebug = ctx.debugRouting();
        if (bDebug) {
            Debug.addRoutingDebug(debugInfo);
        }
    }

    /**
     * 打印 zuul filter  debug 信息
     */
    public static void zuulDebugPrint() {
        RequestContext ctx = RequestContext.getCurrentContext();

        Set<Map.Entry<String, Object>> entries = ctx.entrySet();
        log.info("============== zuul 上下文中所有 key ==================>>");
        entries.forEach(map -> {
            log.info("k:{},v:{}", map.getKey(), map.getValue());
        });
        log.info("<<===================================================");


        log.info("**************************************************************");


        log.info("*************** zuul filter 执行 信息 ***************************");
        Object exeFilter = ctx.get(ZuulConstant.EXECUTED_FILTERS);
        if (exeFilter instanceof StringBuilder) {
            StringBuilder sb = (StringBuilder) exeFilter;
            String[] filters = sb.toString().split(",");
            for (String filter : filters) {
                log.info("{}", filter);
            }
        }
        log.info("***************************************************************");
    }

    /**
     * 获取 请求头信息
     *
     * @param request
     * @return
     */
    public static Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }


    /**
     * 打印 请求参数
     *
     * @param request
     */
    public static void requestParamsPrint(HttpServletRequest request) {

        //请求头
        Map<String, String> headersInfo = getHeadersInfo(request);
        log.info("当前请求头信息 headersInfo:{}", headersInfo);

        //请求参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info(" getParameterMap 网关转换后的请求地址参数为=====>>{}", parameterMap);

        Map<String, List<String>> queryParams = HTTPRequestUtils.getInstance().getQueryParams();
        log.info(" getQueryParams 网关转换后的请求地址参数为======>>{}", queryParams);
    }

    /**
     * 默认网关抛出的异常情况
     * <p>
     * 只有 error 参数的值匹配时才执行 filter
     *
     * @return
     */
    public static boolean requestParamError(RequestContext ctx) {
        HttpServletRequest request = ctx.getRequest();

        String error = request.getParameter(ZuulConstant.MY_ERROR);

        if (StringUtils.isNotBlank(error)) {
            switch (error) {
                case "pre":
                case "route":
                case "error":
                case "post":
                    return true;
                default:
                    return false;
            }
        }

        return false;
    }


    /**
     * 模拟 filter 抛出异常
     * <p>
     * 通过 zuul 全局变量存放异常
     */
    public static void filterThrowError() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        try {
            throw new ZuulException(request.getParameter(
                    ZuulConstant.ERROR),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    request.getParameter(ZuulConstant.MY_ERROR) + "  error");
        } catch (ZuulException ex) {
            //设置状态码
            ctx.set(ZuulConstant.ERROR_STATUS_CODE, ex.nStatusCode);
            // 设置异常信息
            ctx.set(ZuulConstant.ERROR_MESSAGE, ex.errorCause);
            // 设置异常堆栈
            ctx.set(ZuulConstant.ERROR_EXCEPTION, ex);
        } catch (Exception ex) {
            ctx.set(ZuulConstant.ERROR_STATUS_CODE,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ctx.set(ZuulConstant.ERROR_EXCEPTION, ex);
        }
    }


    /**
     * 禁用 filter 继续执行
     */
    public static void disableZuulFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(200);
        ctx.setResponseBody("无权限,禁用 filter 继续执行");
        ctx.getResponse().setContentType(MediaType.TEXT_HTML_VALUE);
    }


    /**
     * 记录 通过网关调用服务的相关信息日志
     */
    public static void zuulCallServiceLog() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Long startTime = (Long) ctx.get(ZuulConstant.SERVICE_START_TIME);
        Long endTime = System.currentTimeMillis() - startTime;

        HttpServletRequest request = ctx.getRequest();
        Map<String, String> zuulRequestHeaders = ctx.getZuulRequestHeaders();

        String clientContext = null;

        Object platformId = ctx.get(ZuulConstant.PLATFORM_ID);
        Object serviceId = ctx.get(ZuulConstant.SERVICE_ID);
        Object requestUrl = ctx.get(ZuulConstant.REQUEST_URI);

        //ribbon code
        Object ribbonResponseCode = ctx.get(ZuulConstant.response_status_code);
        // trace id
        Object traceId = zuulRequestHeaders.get(ZuulConstant.REQUEST_TRAND_ID);
        // 客户端
        String clientIp = zuulRequestHeaders.get(ZuulConstant.REQUEST_X_FORWARDED_FOR);

        Object zuulResponse = ctx.get(ZuulConstant.ZUUL_RESPONSE);
        if (null != zuulRequestHeaders) {
            if (zuulResponse instanceof RibbonHttpResponse) {
                RibbonHttpResponse ribbonHttpResponse = (RibbonHttpResponse) zuulResponse;
                HttpHeaders ribbonHeader = ribbonHttpResponse.getHeaders();
                clientContext = ribbonHeader.get(ZuulConstant.X_APPLICATION_CONTEXT).get(0);
                // 对客户端ip 添加 port 信息
                clientIp = clientIp + clientContext.substring(clientContext.lastIndexOf(":"));
            }
        }

        String gatewayIp = zuulRequestHeaders.get(ZuulConstant.X_FORWARDED_HOST);

        log.info("execution time：{}ms,gatewayIp:{},platformId:{},clientContext:{},serviceId:{},client ip:{},requestUrl:{},ribbonResponseCode:{},traceId:{}",
                endTime, gatewayIp, platformId, clientContext, serviceId, clientIp, requestUrl, ribbonResponseCode, traceId);
    }
}
