package com.example.zuul.filter.pre;

import com.example.zuul.common.ZuulConstant;
import com.example.zuul.dto.TokenDTO;
import com.example.zuul.feign.TokenFeignClient;
import com.example.zuul.util.RoutingUtil;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/8/8
 * Time： 17:57
 * ================================
 */
@Slf4j
//@Component
public class TokenFilter extends ZuulFilter {

    private String authorization = "authorization";

    @Autowired
    private TokenFeignClient tokenFeignClient;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String platformId = request.getParameter("platformId");
        String service = (String) ctx.get(ZuulConstant.SERVICE_ID);

        // 查询 认证头
        String authorizationHeader = request.getHeader(authorization);

        if (StringUtils.isBlank(authorizationHeader)) {
            //请求头为空时，禁止访问
            RoutingUtil.disableZuulFilter();
            return null;
        }


        TokenDTO tokenDTO = checkToken(authorizationHeader);

        // 请求的 platformId 参数 与 token 中的 platformId是否一致
        if (!tokenDTO.getScope().contains(platformId)) {
            // 请求的租户id和授权的platformId 不一致，阻止请求
            RoutingUtil.disableZuulFilter();
        }

        // 检查租户是否有权访问请求的服务
        if (!tokenDTO.getAuthorities().contains(service)) {
            RoutingUtil.disableZuulFilter();
        }

        return null;
    }

    private TokenDTO checkToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer", "").trim();
        TokenDTO tokenDTO = null;

        try {
            tokenDTO = tokenFeignClient.check_token(token);
        } catch (Exception e) {
            log.error("token error:", e);
            return null;
        }

        if (StringUtils.isNotBlank(tokenDTO.getMessage())) {
            throw new RuntimeException(tokenDTO.getMessage());
        }
        return tokenDTO;
    }
}
