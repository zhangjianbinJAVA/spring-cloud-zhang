package com.example.zuul;

import com.google.gson.Gson;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.util.HTTPRequestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
public class RequestParamsPreFilter extends ZuulFilter {

    private final static Logger LOG = LoggerFactory.getLogger(RequestParamsPreFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
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
        ctx.addZuulRequestHeader("X-ECC-Zuul-Forwarded-Domain", currentDomain);



        LOG.info("zuulRequestHeaders:{}", zuulRequestHeaders);

        debugParamLog(request);
        RoutingUtil.zuulDebugPrint(ctx);
        return null;
    }


    private void debugParamLog(HttpServletRequest request) {
        Gson gson = new Gson();
        Map<String, String[]> parameterMap = request.getParameterMap();
        String result = gson.toJson(parameterMap);
        LOG.info("getParameterMap 网关转换后的请求地址参数为=====>>{}", result);

        Map<String, List<String>> queryParams = HTTPRequestUtils.getInstance().getQueryParams();
        String result2 = gson.toJson(queryParams);
        LOG.info("getQueryParams网关转换后的请求地址参数为=====>>{}", result2);


        Map<String, String> headersInfo = getHeadersInfo(request);
        LOG.info("当前请求头信息 headersInfo:{}", gson.toJson(headersInfo));
    }

    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        String forwardedHost = request.getHeader("x-forwarded-host");
        LOG.info("x-forwarded-host:{}", forwardedHost);

        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }


}
