package com.example.zuul.common;

import static com.example.zuul.common.ZuulConstant.*;

public enum CustomFilter {


    /**
     * pre
     */
    CustomZuulStartTimeFilter(PRE, Integer.MIN_VALUE),
    CustomRequestParamFilter(PRE, 50),

    CustomRequestHeaderFilter(PRE, 51),

    CustomThrowErrorPreFilter(PRE, 52),
    CustomDisableFilter(PRE, 53),

    CustomHelloFeignClientFilter(PRE, 54),

    /**
     * route
     */
    CustomRoutingFilter(ROUTE, 100),

    CustomThrowErrorRouteFilter(ROUTE, 101),

    /**
     * post
     */
    CustomThrowErrorPostFilter(POST, 151),

    CustomZuulCallServiceLogFilter(POST, 998),

    CustomResponseCodeFilter(POST, 990),//大于 SendResponseFilter 的执行

    CustomZuulFilterDebugFilter(POST, Integer.MAX_VALUE),

    /**
     * error
     */
    CustomFilterErrorFilter(ERROR, 500);


    private int filterOrder;
    private String filterType;

    CustomFilter(String filterType, int filterOrder) {
        this.filterType = filterType;
        this.filterOrder = filterOrder;
    }

    public int getFilterOrder() {
        return filterOrder;
    }

    public void setFilterOrder(int filterOrder) {
        this.filterOrder = filterOrder;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }
}
