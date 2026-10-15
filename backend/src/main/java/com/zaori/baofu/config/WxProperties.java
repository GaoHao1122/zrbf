package com.zaori.baofu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置，对应 application.yml 中的 wx.miniapp
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxProperties {

    /** 小程序 AppID */
    private String appid;

    /** 小程序 AppSecret */
    private String secret;

    /** 开发模式：true 时不调微信 code2session，使用固定开发用户 */
    private boolean mockLogin = true;
}
