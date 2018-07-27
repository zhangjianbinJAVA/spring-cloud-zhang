package com.myke.zuul.web.ip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@RestController
public class CallWeb {

    @Autowired
    private HttpServletRequest request;

    @PostMapping("/call")
    public String getCall(@RequestParam(required = false) String regionId) {
        Enumeration<String> parameterNames = request.getParameterNames();
        return "111";
    }


    //简单格式的消息通知
    @RequestMapping("/notifications1")
    public String notifications1(@RequestBody String message
            , @RequestHeader HttpHeaders headers) {

        if (headers.get("x-jdcloud-message-type").get(0).equals("SubscriptionConfirmation")) {
            //设置时对url的校验，需要对message进行base64编码并返回
            return Base64Utils.encodeToString(message.getBytes(StandardCharsets.UTF_8));
        } else {
            //消息通知处理  your code，处理完毕后需要返回 http code 200，body不做校验
            return "";
        }
    }

    //JSON格式的消息通知
    @PostMapping("/notification2")
    String notification2(@RequestBody Notification notification
            , @RequestHeader HttpHeaders headers) {
        if (headers.get("x-jdcloud-message-type").get(0).equals("SubscriptionConfirmation")) {
            //设置时对url的校验，需要对message进行base64编码并返回
            return Base64Utils.encodeToString(notification.getMessage().getBytes(StandardCharsets.UTF_8));
        } else {
            //消息通知处理  your code，处理完毕后需要返回 http code 200，body不做校验
            return "";
        }
    }

    //JSON格式的消息通知体
    public class Notification {
        private String topicOwner;
        private String topicName;
        private String subscriptionName;
        private String messageId;
        private String message;
        private String messageMD5;
        private String messageTag;
        private long publishTime;

        public Notification() {
        }

        public Notification(String topicOwner, String topicName, String subscriptionName, String messageId, String message
                , String messageMD5, String messageTag, long publishTime) {
            this.topicOwner = topicOwner;
            this.topicName = topicName;
            this.subscriptionName = subscriptionName;
            this.messageId = messageId;
            this.message = message;
            this.messageMD5 = messageMD5;
            this.messageTag = messageTag;
            this.publishTime = publishTime;
        }

        public String getTopicOwner() {
            return topicOwner;
        }

        public void setTopicOwner(String topicOwner) {
            this.topicOwner = topicOwner;
        }

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        public String getSubscriptionName() {
            return subscriptionName;
        }

        public void setSubscriptionName(String subscriptionName) {
            this.subscriptionName = subscriptionName;
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessageMD5() {
            return messageMD5;
        }

        public void setMessageMD5(String messageMD5) {
            this.messageMD5 = messageMD5;
        }

        public String getMessageTag() {
            return messageTag;
        }

        public void setMessageTag(String messageTag) {
            this.messageTag = messageTag;
        }

        public long getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(long publishTime) {
            this.publishTime = publishTime;
        }
    }
}
