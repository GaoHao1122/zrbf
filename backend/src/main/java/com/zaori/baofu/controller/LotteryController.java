package com.zaori.baofu.controller;

import com.zaori.baofu.common.Result;
import com.zaori.baofu.common.UserContext;
import com.zaori.baofu.dto.request.LotteryDetailAddRequest;
import com.zaori.baofu.dto.request.LotteryDetailUpdateRequest;
import com.zaori.baofu.dto.request.LotteryListRequest;
import com.zaori.baofu.dto.response.DailyRecordResponse;
import com.zaori.baofu.dto.response.LotteryDetailAddResponse;
import com.zaori.baofu.dto.response.LotteryListItem;
import com.zaori.baofu.dto.response.LotteryListResponse;
import com.zaori.baofu.service.LotteryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 彩票记录模块（核心）
 * 对应文档：6.接口设计.md 4 彩票记录模块
 */
@RestController
@RequestMapping("/api/v1/lottery")
@RequiredArgsConstructor
public class LotteryController {

    private final LotteryService lotteryService;

    /**
     * 4.1 新增彩票明细
     * 同事务内实时聚合主记录
     */
    @PostMapping
    public Result<LotteryDetailAddResponse> addDetail(@Valid @RequestBody LotteryDetailAddRequest request) {
        Long userId = getCurrentUserId();
        return Result.success(lotteryService.addDetail(userId, request));
    }

    /**
     * 4.2 查询每日记录（含明细列表）
     */
    @GetMapping("/detail/{date}")
    public Result<DailyRecordResponse> getDailyDetail(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = getCurrentUserId();
        return Result.success(lotteryService.getDailyDetail(userId, date));
    }

    /**
     * 4.3 历史记录列表（分页）
     * 返回结构：{total, list}
     */
    @GetMapping("/list")
    public Result<LotteryListResponse> list(LotteryListRequest request) {
        Long userId = getCurrentUserId();
        return Result.success(lotteryService.listRecords(userId, request));
    }

    /**
     * 4.4 修改彩票明细
     */
    @PutMapping("/detail/{id}")
    public Result<LotteryDetailAddResponse> updateDetail(
            @PathVariable Long id,
            @Valid @RequestBody LotteryDetailUpdateRequest request) {
        Long userId = getCurrentUserId();
        return Result.success(lotteryService.updateDetail(userId, id, request));
    }

    /**
     * 4.5 删除彩票明细
     */
    @DeleteMapping("/detail/{id}")
    public Result<Void> deleteDetail(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        lotteryService.deleteDetail(userId, id);
        return Result.success();
    }

    /**
     * 4.6 清空当日全部明细
     * 同步删除主记录
     */
    @DeleteMapping("/clear/{date}")
    public Result<Void> clearDaily(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = getCurrentUserId();
        lotteryService.clearDaily(userId, date);
        return Result.success();
    }

    private Long getCurrentUserId() {
        return UserContext.getUserId();
    }
}
