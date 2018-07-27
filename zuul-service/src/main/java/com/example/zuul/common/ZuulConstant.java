package com.example.zuul.common;

public class ZuulConstant {

    /******************** filter type********************************/

    /**
     * 路由前执行
     */
    public static final String PRE = "pre";

    /**
     * 在路由请求时候被调用
     */
    public static final String ROUTE = "route";

    /**
     * 路由后执行
     */
    public static final String POST = "post";

    /**
     * 发生错误时
     */
    public static final String ERROR = "error";

    /****************************** 自定义 常量************************************/

    /**
     * 网关执行开始时间
     */
    public static final String SERVICE_START_TIME = "service_start_time";

    /**
     * 网关执行结否时间
     */
    public static final String SERVICE_END_TIME = "service_start_time";

    /**
     * 租户id
     */
    public static final String PLATFORM_ID = "platformId";


    /**
     * 自写异常字段
     */
    public static final String MY_ERROR = "myError"; // 错误信息


    /****************************** zuul 常量************************************/

    /**
     * 路由的服务名
     */
    public static final String SERVICE_ID = "serviceId";

    /**
     * ribbon response 响应状态
     */
    public static final String response_status_code = "responseStatusCode";

    /**
     * 请求 url
     */
    public static final String REQUEST_URI = "requestURI";


    /**
     * 请求 traceId
     */
    public static final String REQUEST_TRAND_ID = "X-B3-TraceId";

    /**
     * 网关 路由微服务的真实的IP
     */
    public static final String REQUEST_X_FORWARDED_FOR = "x-forwarded-for";


    /**
     * 当前网关的 ip 地址 + 端口 信息
     */
    public static final String X_FORWARDED_HOST = "x-forwarded-host";

    /**
     * 网关响应头
     */
    public static final String ZUUL_RESPONSE = "zuulResponse";

    /**
     * 网关路由服务的上下文信息   服务名:当前环境：服务的端口号  例如：hello-service-1:dev:18081
     */
    public static final String X_APPLICATION_CONTEXT = "X-Application-Context";

    /**
     * 网关执行 filter  信息
     */
    public static final String EXECUTED_FILTERS = "executedFilters";


    /**************************** zuul error 常量 ****************************************/
    public static final String ERROR_STATUS_CODE = "error.status_code"; // 错误编码

    public static final String ERROR_EXCEPTION = "error.exception"; // Exception 对象

    public static final String ERROR_MESSAGE = "error.message"; // 错误信息


}
