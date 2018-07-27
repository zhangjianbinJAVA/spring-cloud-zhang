package com.myke.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/7/12
 * Time： 18:45
 * ================================
 */
@Component
public class CustomFilter extends ZuulFilter {
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
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(401);
        ctx.setResponseBody("aa");
        ctx.getResponse().setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        return null;
    }
}
