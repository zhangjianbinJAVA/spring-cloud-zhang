package com.example.zuul.filter.route;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/9/21 11:43
 * ================================
 */
@Slf4j
@Component
public class AggregationRequestFilter extends ZuulFilter {

    private static final Set<String> endpoints = new HashSet<String>(Arrays.asList("_isok", "features", "autoconfig", "archaius", "beans", "pause", "configprops", "resume", "mappings", "channels", "restart", "health", "env", "metrics", "trace", "dump", "jolokia", "info", "logfile", "refresh", "flyway", "liquibase", "heapdump", "loggers", "auditevents"));


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 6;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext cx = RequestContext.getCurrentContext();
        String proxy = (String) cx.get("proxy");
        return "admin".contains(proxy);
    }

    @Override
    public Object run() {
        RequestContext cx = RequestContext.getCurrentContext();
        HttpServletRequest request = cx.getRequest();
        log.info("requestURL:{}", request.getRequestURI());
        String requestURI = request.getRequestURI();

        final String copyRequestUrl = requestURI;

        //// swagger 访问不处理
        //if (StringUtils.contains(requestURI, "swagger") || StringUtils.contains(requestURI, "api-docs")) {
        //    return null;
        //} else if (StringUtils.contains(request.getHeader("referer"), "swagger")) {
        //    // 通过原有服务 sagger 访问请求路径时，第一个前缀去掉
        //    requestURI = requestURI.replaceFirst("/" + cx.get("proxy"), "");
        //} else if (endpoints.stream().filter(e -> copyRequestUrl.contains(e)).count() > 0) {
        //    // 请求端点处理
        //    log.info("endpoint url", requestURI);
        //
        //    String[] endpointSplit = requestURI.split("/");
        //    Set<String> endpointUrlResult = new HashSet<>(Arrays.asList(endpointSplit));
        //    // 求集合交集,结果大于0时，则说明是端点请求，不做任何处理
        //    endpointUrlResult.retainAll(endpoints);
        //    if (endpointUrlResult.size() > 0) {
        //        return null;
        //    }
        //}

        // ======================= 聚合服务功能处理,改变路由的请求 url 到聚合服务上 ======================================

        String new_requestURL = "/ecc-admin" + requestURI;

        log.info("new_requestURL:{}", new_requestURL);
        //修改请求url
        cx.set("requestURI", new_requestURL);

        //请求头
        cx.getZuulRequestHeaders().put("Authorization", "Basic YWRtaW46YWRtaW4=");
        return null;
    }

    //
    //@Test
    //public void endpointsTest() {
    //    String url = "/shop/seller/shopinfo/queryByShopId/infos";
    //    Set<String> endpoints = new HashSet<>(Arrays.asList("_isok", "features", "autoconfig", "archaius", "beans", "pause", "configprops", "resume", "mappings", "channels", "restart", "health", "env", "metrics", "trace", "dump", "jolokia", "info", "logfile", "refresh", "flyway", "liquibase", "heapdump", "loggers", "auditevents"));
    //
    //    if (endpoints.stream().filter(e -> url.contains(e)).count() > 0) {
    //        String[] split = url.split("/");
    //        Set<String> endpoints_url_list = new HashSet<String>(Arrays.asList(split));
    //        endpoints_url_list.retainAll(endpoints);
    //        System.out.println(endpoints_url_list);
    //    } else {
    //        System.out.println("no url");
    //    }
    //}
}

