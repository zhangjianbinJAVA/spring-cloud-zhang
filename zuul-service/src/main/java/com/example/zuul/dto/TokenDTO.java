package com.example.zuul.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/8/8
 * Time： 18:29
 * ================================
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenDTO {


    /**
     * scope : ["1"]
     * exp : 1533725605
     * authorities : ["order-service","user-service"]
     * client_id : xyx
     */

    private int exp;
    private String client_id;
    private List<String> scope;
    private List<String> authorities;

    /**
     * error
     */
    private String error;
    private String message;
    private String path;

}
