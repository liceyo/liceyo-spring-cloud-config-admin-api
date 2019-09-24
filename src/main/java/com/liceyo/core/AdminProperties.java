package com.liceyo.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AdminProperties
 * @description 配置
 * @author lichengyong
 * @date 2019/9/12 11:44
 * @version 1.0
 */
@Data
@SuppressWarnings("ConfigurationProperties")
@ConfigurationProperties(prefix = "spring.cloud.config.admin")
public class AdminProperties {
    public static final String REFRESH_WAY_HTTP = "http";
    public static final String REFRESH_WAY_MQ = "mq";
    /** 刷新方式 **/
    private String refreshWay = REFRESH_WAY_HTTP;
    /** 刷新接口 **/
    private String refreshUrl = "/actuator/refresh";
    /** 应用信息文件 **/
    private String applicationInfoFile = "classpath:admin-application.txt";
    /** 用户名 **/
    private String username = "admin";
    /** 密码 **/
    private String credentials = "admin";
}
