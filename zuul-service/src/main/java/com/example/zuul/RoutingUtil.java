package com.example.zuul;

import com.netflix.zuul.context.Debug;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.apachecommons.CommonsLog;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author zhangjianbin
 * @version v1.0
 * @date 2017/7/26 20:24
 */
@CommonsLog
public class RoutingUtil {

    /**
     * 记录 路由 debug 信息
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
     * 打印 zuul 路由 debug 信息
     *
     * @param ctx
     */
    public static void zuulDebugPrint(RequestContext ctx) {

        Set<Map.Entry<String, Object>> entries = ctx.entrySet();
        log.info("================================>>");
        entries.forEach(k -> {
            log.info("zuul info RequestContext key:" + k);
        });

        // 获取 路由debug信息 常量在RequestContext类中
        Object routingDebug = ctx.get("routingDebug");
        if (routingDebug instanceof ArrayList) {
            List<String> list = (ArrayList) routingDebug;
            list.forEach(s -> {
                log.info("zuul debug routingDebug:" + s);
            });
        }
        log.info("<<================================");
    }

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
}
