package com.zaori.baofu.controller;

import com.zaori.baofu.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 服务状态接口
 */
@RestController
public class SystemController {

    @GetMapping("/")
    public Result<Map<String, String>> index() {
        return Result.success(Map.of(
                "service", "zaori-baofu",
                "status", "UP"
        ));
    }
}
