package com.zaori.baofu.service;

import java.util.Map;

/**
 * 管理后台服务（预留）
 */
public interface AdminService {

    /**
     * 用户列表
     */
    Map<String, Object> listUsers(Integer page, Integer size);

    /**
     * 全站统计
     */
    Map<String, Object> getGlobalStatistics();
}
