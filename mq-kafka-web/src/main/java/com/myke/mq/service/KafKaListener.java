package com.myke.mq.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/7/17
 * Time： 11:20
 * ================================
 */

@Slf4j
@Service
public class KafKaListener {

    @KafkaListener(topics = "gateway-log")
    public void gatewayLogListen(ConsumerRecord<?, ?> cr) {
        log.info("{} - {} : {}", cr.topic(), cr.key(), cr.value());
    }
}
