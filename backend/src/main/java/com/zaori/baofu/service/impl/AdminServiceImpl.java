package com.zaori.baofu.service.impl;

import com.zaori.baofu.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Override
    public Map<String, Object> listUsers(Integer page, Integer size) {
        // TODO
        return Map.of();
    }

    @Override
    public Map<String, Object> getGlobalStatistics() {
        // TODO
        return Map.of();
    }
}
