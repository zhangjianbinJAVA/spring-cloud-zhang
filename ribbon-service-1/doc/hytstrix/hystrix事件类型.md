常见的hystrix事件类型

1. run()
    - SUCCESS：run()成功，不触发getFallback()
    - FAILURE：run()抛异常，触发getFallback()
    - TIMEOUT：run()超时，触发getFallback()
    - BAD_REQUEST：run()抛出HystrixBadRequestException，不触发getFallback()
    - SHORT_CIRCUITED：断路器开路，触发getFallback()
    - THREAD_POOL_REJECTED：线程池耗尽，触发getFallback()
    - FALLBACK_MISSING：没有实现getFallback()，抛出异常
2. getFallback()
    - FALLBACK_SUCCESS：getFallback()成功，不抛异常
    - FALLBACK_FAILURE：getFallback()失败，抛异常
    - FALLBACK_REJECTION：调用getFallback()的线程数超量，抛异常