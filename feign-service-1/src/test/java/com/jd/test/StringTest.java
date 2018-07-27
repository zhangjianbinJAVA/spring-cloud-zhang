//package com.jd.test;
//
//import org.junit.Test;
//
//import java.net.URI;
//
///**
// * @author： zhangjianbin <br/>
// * ===============================
// * Created with IDEA.
// * Date： 2018/6/26
// * Time： 14:48
// * ================================
// */
//public class StringTest {
//
//    @Test
//    public void str() {
//        String url = "http://HELLO-SERVICE-1/request-body?token=ZGVsZXRlX3Rva2VuX2J5X2Jhc2U2NA%3D%3D";
//        URI uri = URI.create(url);
//        String clientName = uri.getHost();
//        String s = url.replaceFirst(clientName,  clientName+"/user/moke/test/"+clientName);
//        System.out.println(s);
//    }
//}
