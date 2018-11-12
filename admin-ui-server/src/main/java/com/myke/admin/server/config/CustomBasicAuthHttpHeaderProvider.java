package com.myke.admin.server.config;

import de.codecentric.boot.admin.model.Application;
import de.codecentric.boot.admin.web.client.HttpHeadersProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/10/22 16:03
 * ================================
 */
public class CustomBasicAuthHttpHeaderProvider implements HttpHeadersProvider {
    @Override
    public HttpHeaders getHeaders(Application application) {
        String username = "ecc-pass-endpoint";
        String password = "KTn68]7b!ClaD$OT;";
        System.out.println("username:{}" + username + ",password:{}" + password);
        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            headers.set("Authorization", this.encode(username, password));
        }
        return headers;
    }

    protected String encode(String username, String password) {
        String token = Base64Utils.encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        return "Basic " + token;
    }
}
