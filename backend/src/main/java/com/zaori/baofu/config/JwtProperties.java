package com.zaori.baofu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置，对应 application.yml 中的 jwt
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** 签名密钥，生产环境必须通过 JWT_SECRET 环境变量覆盖 */
    private String secret;

    /** 有效期（秒），默认 7 天 */
    private Long expiration = 604800L;
}
