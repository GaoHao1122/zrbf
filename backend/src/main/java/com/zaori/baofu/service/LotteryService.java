package com.zaori.baofu.service;

import com.zaori.baofu.dto.request.LotteryDetailAddRequest;
import com.zaori.baofu.dto.request.LotteryDetailUpdateRequest;
import com.zaori.baofu.dto.request.LotteryListRequest;
import com.zaori.baofu.dto.response.DailyRecordResponse;
import com.zaori.baofu.dto.response.LotteryDetailAddResponse;
import com.zaori.baofu.dto.response.LotteryListResponse;

import java.time.LocalDate;

/**
 * 彩票记录服务（核心）
 *
 * 设计要点：
 * - 明细变更（新增/修改/删除）时，在同一事务内实时更新主表聚合值
 * - 保证主表 lottery_record 与明细表 lottery_detail 数据一致性
 */
public interface LotteryService {

    /**
     * 新增彩票明细
     * 同事务内：1)取/建主记录 2)写明细 3)实时聚合主记录
     */
    LotteryDetailAddResponse addDetail(Long userId, LotteryDetailAddRequest request);

    /**
     * 查询每日记录（含明细列表）
     */
    DailyRecordResponse getDailyDetail(Long userId, LocalDate date);

    /**
     * 历史记录列表（分页）
     * 返回结构：{total, list}
     */
    LotteryListResponse listRecords(Long userId, LotteryListRequest request);

    /**
     * 修改彩票明细
     * 同事务内：1)改明细 2)实时聚合主记录
     */
    LotteryDetailAddResponse updateDetail(Long userId, Long detailId, LotteryDetailUpdateRequest request);

    /**
     * 删除彩票明细
     * 同事务内：1)删明细 2)实时聚合主记录（无明细则删主记录）
     */
    void deleteDetail(Long userId, Long detailId);

    /**
     * 清空当日全部明细
     * 同事务内：1)删全部明细 2)删主记录
     */
    void clearDaily(Long userId, LocalDate date);
}
