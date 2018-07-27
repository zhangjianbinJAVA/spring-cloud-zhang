//package com.myke.doc;
//
//import com.myke.hello.HelloApplication;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import static java.util.concurrent.TimeUnit.SECONDS;
//
//@Slf4j
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = HelloApplication.class)
//@EnableAutoConfiguration
//public class RedisTemplateTest {
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    @Test
//    public void redisIncrTest() {
//        Long zhangjianbin = this.redisTemplate.boundValueOps("zhangjianbin").increment(0);
//
//        log.info("redis result:{}", zhangjianbin);
//    }
//
//    @Test
//    public void getExpire() {
//        Boolean zhangjianbin = this.redisTemplate.expire("zhangjianbin", 10, SECONDS);
//        Long expire = this.redisTemplate.getExpire("zhangjianbin");
//        log.info("expire result:{}", expire);
//    }
//
//    @Test
//    public void ttl(){
//
//    }
//
//}
