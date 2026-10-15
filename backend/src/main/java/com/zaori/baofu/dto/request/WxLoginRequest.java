package com.zaori.baofu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录请求
 * 对应接口：POST /api/v1/user/login
 */
@Data
public class WxLoginRequest {

    @NotBlank(message = "code不能为空")
    private String code;
}
