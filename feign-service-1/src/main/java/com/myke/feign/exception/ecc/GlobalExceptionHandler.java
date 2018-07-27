//package com.myke.feign.exception.ecc;
//
//import com.netflix.hystrix.exception.HystrixBadRequestException;
//import com.netflix.hystrix.exception.HystrixRuntimeException;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
//import org.springframework.cloud.sleuth.Tracer;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.HttpMessageConversionException;
//import org.springframework.validation.BindException;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.MissingServletRequestParameterException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.ConstraintViolation;
//import javax.validation.ConstraintViolationException;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///**
// * Created by lilaien on 2017/1/20.
// * <p>
// * 全局异常处理类
// */
//@ControllerAdvice
//public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
//    public static final String PARAM_VALID_ERROR_CODE = "0-0012";
//
//    public static final String PARAM_VALID_ERROR_MSG = "参数校验错误: ";
//
//    private Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
//
//    @Autowired
//    private DefaultErrorAttributes defaultErrorAttributes;
//
//    @Autowired
//    private HttpServletRequest request;
//
//    @Autowired
//    private Tracer tracer;
//
//    @Value("${ecc.error.debug:false}")
//    private Boolean ECC_ERROR_DEBUG = false;
//
//    /**
//     * 时间戳转日期
//     *
//     * @param timestamp
//     * @return
//     */
//    private String timestampToDate(Object timestamp) {
//        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            if (timestamp instanceof Date) {
//                Date result = (Date) timestamp;
//                return d.format(result);
//            }
//        } catch (NumberFormatException e) {
//            LOG.error("时间戳转日期异常:error:{}", e);
//        }
//        Date defaultDate = new Date();
//        return d.format(defaultDate);
//    }
//
//    /**
//     * 服务端业务异常
//     *
//     * @param exception
//     * @return
//     */
//    @ExceptionHandler(value = BaseException.class)
//    public ResponseEntity<String> serviceException(BaseException exception) {
//        return handleBaseException(exception);
//    }
//
//    /**
//     * 系统未知异常
//     *
//     * @param exception
//     * @return
//     */
//    @ExceptionHandler(value = Exception.class)
//    public ResponseEntity<Object> systemException(Exception exception) {
//        Map<String, Object> errorAttributes = errorAttributes();
//        errorAttributes.put("msg", ResponseCode.EXCEPTION_CODE.getMsg());
//        errorAttributes.put("code", ResponseCode.EXCEPTION_CODE.getCode());
//        if (exception instanceof NullPointerException) {
//            errorAttributes.put("message", "空指针异常");
//        }
//        LOG.error("系统异常信息,result:{},error:{}", errorAttributes, exception);
//        return getResponseEntity(errorAttributes, HttpStatus.INTERNAL_SERVER_ERROR, exception);
//    }
//
//    private ResponseEntity getResponseEntity(Map<String, Object> errorAttributes, HttpStatus status,
//            Exception ex) {
//
//        // 移除sql
//        sqlIgnoreMessage(errorAttributes);
//
//        // 添加请求链id
//        String traceId = tracer.getCurrentSpan().traceIdString();
//        errorAttributes.put("traceId", traceId);
//        tracer.addTag("error", ex.getMessage());
//        tracer.close(tracer.getCurrentSpan());
//
//        if (!ECC_ERROR_DEBUG) {// 默认情况下要移除的字段
//            errorAttributes.remove("trace");
//            errorAttributes.remove("exception");
//        }
//
//        return new ResponseEntity(errorAttributes, status);
//    }
//
//    private void sqlIgnoreMessage(Map<String, Object> errorAttributes) {
//        String exception = (String) errorAttributes.get("exception");
//        if (StringUtils.containsIgnoreCase(exception, "jdbc")
//                || StringUtils.containsIgnoreCase(exception, "sql")) {
//            errorAttributes.put("message", "sql异常");
//        }
//    }
//
//    /**
//     * feign client Hystrix 异常
//     *
//     * @param exception
//     * @return
//     */
//    @ExceptionHandler(value = { HystrixRuntimeException.class, HystrixBadRequestException.class })
//    public ResponseEntity hystrixException(Exception exception) {
//        Map<String, Object> errorAttributes = new HashMap<>();
//        if (exception instanceof HystrixRuntimeException) {
//            if (exception.getCause() instanceof ServiceServerException) {
//                ServiceServerException ex = (ServiceServerException) exception.getCause();
//                errorAttributes = ex.getMap();
//                errorAttributes.put("msg", ResponseCode.REMOTE_CALL_SERVICE_CODE.getMsg());
//                errorAttributes.put("code", ResponseCode.REMOTE_CALL_SERVICE_CODE.getCode());
//                LOG.error("远程调用服务端异常信息,result:{}", errorAttributes);
//            } else {
//                HystrixRuntimeException hystrixRuntimeException = (HystrixRuntimeException) exception;
//                HystrixRuntimeException.FailureType failureType = hystrixRuntimeException.getFailureType();
//                String fallbackException = hystrixRuntimeException.getFallbackException().getClass()
//                        .toString();
//                errorAttributes.put("error", failureType);
//                errorAttributes.put("exception", fallbackException);
//                errorAttributes.put("message",
//                        hystrixRuntimeException.getCause() != null
//                                ? hystrixRuntimeException.getCause().getMessage()
//                                : hystrixRuntimeException.getMessage());
//                errorAttributes.put("msg", ResponseCode.REMOTE_CALL_HYSTRIXRUNTIME_CODE.getMsg());
//                errorAttributes.put("code", ResponseCode.REMOTE_CALL_HYSTRIXRUNTIME_CODE.getCode());
//                errorAttributes = addStackTrace(errorAttributes, hystrixRuntimeException);
//                LOG.error("远程调用运行时异常信息,result:{},error:{}", errorAttributes, hystrixRuntimeException);
//            }
//        } else if (exception instanceof ServiceClientException) {
//            ServiceClientException serviceClientException = (ServiceClientException) exception;
//            Map<String, Object> map = serviceClientException.getMap();
//            errorAttributes = map;
//            errorAttributes.put("msg", ResponseCode.REMOTE_CALL_CLIENT_CODE.getMsg());
//            errorAttributes.put("code", ResponseCode.REMOTE_CALL_CLIENT_CODE.getCode());
//            LOG.error("远程调用客户端异常信息,result:{},error:{}", errorAttributes, serviceClientException);
//        }
//
//        errorAttributes.put("timestamp", timestampToDate(new Date()));
//        return getResponseEntity(errorAttributes, HttpStatus.INTERNAL_SERVER_ERROR, exception);
//    }
//
//    /**
//     * 异常栈打印
//     *
//     * @param errorAttributes
//     * @param error
//     * @return
//     */
//    private Map<String, Object> addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
//        StringWriter stackTrace = new StringWriter();
//        error.printStackTrace(new PrintWriter(stackTrace));
//        stackTrace.flush();
//        errorAttributes.put("trace", stackTrace.toString());
//        return errorAttributes;
//    }
//
//    /**
//     * 处理BaseException
//     *
//     * @param exception exception
//     * @return String
//     */
//    private ResponseEntity handleBaseException(BaseException exception) {
//        Map<String, Object> errorAttributes = errorAttributes();
//        errorAttributes.put("msg", exception.getMsg());
//        errorAttributes.put("code", exception.getCode());
//        LOG.error("业务返回异常信息,result:{}", errorAttributes);
//        return getResponseEntity(errorAttributes, HttpStatus.INTERNAL_SERVER_ERROR, exception);
//    }
//
//    /**
//     * add by Baibing @2017-5-17 参数校验异常，用于拦截service参数校验抛出的异常信息
//     * <p>
//     * 不使用第三方包的参数校验异常处理
//     *
//     * @param exception ConstraintViolationException
//     * @return string
//     */
//    @ExceptionHandler(value = ConstraintViolationException.class)
//    public ResponseEntity violationException(ConstraintViolationException exception) {
//        Map<String, Object> errorAttributes = errorAttributes();
//        List result = new ArrayList();
//        Set<ConstraintViolation<?>> errorList = exception.getConstraintViolations();
//        errorList.forEach(error -> result.add(error.getMessage()));
//        errorAttributes.put("message", result);
//        StringBuilder sb = new StringBuilder(PARAM_VALID_ERROR_MSG);
//        errorList.forEach(error -> sb.append(error.getMessage()).append(" "));
//        errorAttributes.put("msg", sb.toString());
//        errorAttributes.put("code", PARAM_VALID_ERROR_CODE);
//        LOG.error("参数校验异常:result:{},error:{}", errorAttributes, exception.getConstraintViolations());
//        return getResponseEntity(errorAttributes, HttpStatus.BAD_REQUEST, exception);// 参数校验状态改为 400
//    }
//
//    /**
//     * 通用接口异常处理
//     */
//    @Override
//    protected ResponseEntity handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
//            HttpStatus status, WebRequest request) {
//        Map<String, Object> errorAttributes = errorAttributes();
//        // 使用第三方包，参数校验时 异常处理
//        // 当Bean validation失败的时候，会抛出 MethodArgumentNotValidException
//        if (ex instanceof MethodArgumentNotValidException) {
//            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) ex;
//            List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
//            List<ArgumentInvalidResult> invalidArguments = getArgumentInvalidResults(fieldErrors);
//
//            errorAttributes.put("msg", ResponseCode.NOT_VALID_ARGUMENT_CODE.getMsg());
//            errorAttributes.put("code", ResponseCode.NOT_VALID_ARGUMENT_CODE.getCode());
//            errorAttributes.put("message", invalidArguments);
//            LOG.error("参数校验异常:result:{},error:{}", errorAttributes, exception);
//            // 参数类型转换异常
//        } else if (ex instanceof BindException) {
//            BindException exception = (BindException) ex;
//            List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
//            List<ArgumentInvalidResult> argumentInvalidResults = getArgumentInvalidResults(fieldErrors);
//            StringBuilder sb = new StringBuilder(PARAM_VALID_ERROR_MSG);
//            argumentInvalidResults.stream().forEach(argumentInvalidResult -> {
//                sb.append(argumentInvalidResult.getDefaultMessage()).append(" ");
//            });
//
//            errorAttributes.put("msg", sb.toString());
//            errorAttributes.put("code", ResponseCode.NOT_VALID_ARGUMENT_CODE.getCode());
//            errorAttributes.put("message", argumentInvalidResults);
//            LOG.error("参数校验异常:result:{},error:{}", errorAttributes, exception);
//        } else if (ex instanceof MethodArgumentTypeMismatchException) {
//            MethodArgumentTypeMismatchException exception = (MethodArgumentTypeMismatchException) ex;
//            errorAttributes.put("msg", ResponseCode.PARAMETER_CONVERSION_ERROR_CODE.getMsg());
//            errorAttributes.put("code", ResponseCode.PARAMETER_CONVERSION_ERROR_CODE.getCode());
//            errorAttributes.put("error", String.format("异常参数:%s", exception.getName()));// 异常参数
//            LOG.error("参数类型转换异常，方法：{},参数：{},信息:{},result:{},error:{}",
//                    exception.getParameter().getMethod().getName(), exception.getName(),
//                    exception.getLocalizedMessage(), errorAttributes, exception);
//            // 缺少参数异常
//        } else if (ex instanceof MissingServletRequestParameterException) {
//            MissingServletRequestParameterException exception = (MissingServletRequestParameterException) ex;
//            errorAttributes.put("msg", ResponseCode.MISSING_REQUESTPA_RAMETER_CODE.getMsg());
//            errorAttributes.put("code", ResponseCode.MISSING_REQUESTPA_RAMETER_CODE.getCode());
//            errorAttributes.put("error", String.format("缺少参数:%s", exception.getParameterName()));// 缺少参数
//            LOG.error("缺少参数异常,result:{},error:{}", errorAttributes, exception);
//            // spring mvc消息转换器异常
//        } else if (ex instanceof HttpMessageConversionException) {
//            HttpMessageConversionException exception = (HttpMessageConversionException) ex;
//            errorAttributes.put("msg", ResponseCode.SPRING_MVC_HTTPMESSAGE_CONVERSION_CODE.getMsg());
//            errorAttributes.put("code", ResponseCode.SPRING_MVC_HTTPMESSAGE_CONVERSION_CODE.getCode());
//            LOG.error("spring mvc消息转换器 参数序列化异常异常,result:{},error:{}", errorAttributes, exception);
//        }
//        return getResponseEntity(errorAttributes, status, ex);
//    }
//
//    private List<ArgumentInvalidResult> getArgumentInvalidResults(List<FieldError> exception) {
//        // 按需重新封装需要返回的错误信息
//        List<ArgumentInvalidResult> invalidArguments = new ArrayList<>();
//        // 解析原错误信息，封装后返回，此处返回非法的字段名称，原始值，错误信息
//        for (FieldError error : exception) {// 获取所有参数绑定无效异常
//            ArgumentInvalidResult invalidArgument = new ArgumentInvalidResult();// 返回异常信息
//            invalidArgument.setDefaultMessage(error.getDefaultMessage());
//            invalidArgument.setField(error.getField());
//            invalidArgument.setRejectedValue(error.getRejectedValue());
//            invalidArguments.add(invalidArgument);
//        }
//        if (invalidArguments.size() == 0) {
//            ArgumentInvalidResult argumentInvalidResult = new ArgumentInvalidResult();
//            argumentInvalidResult.setDefaultMessage("无错误信息");
//            invalidArguments.add(argumentInvalidResult);
//        }
//        return invalidArguments;
//    }
//
//    /**
//     * spring boot 提供的异常字段
//     *
//     * @return
//     */
//    private Map<String, Object> errorAttributes() {
//        Boolean debug = true;
//        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
//        Map<String, Object> errorAttributes = defaultErrorAttributes.getErrorAttributes(requestAttributes,
//                debug);
//        // 字段格式化
//        errorAttributes.put("timestamp", timestampToDate(errorAttributes.get("timestamp")));
//        // 移除字段
//        errorAttributes.remove("status");
//        errorAttributes.remove("errors");
//        return errorAttributes;
//    }
//}