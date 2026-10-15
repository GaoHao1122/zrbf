package com.zaori.baofu.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zaori.baofu.dto.request.LotteryDetailAddRequest;
import com.zaori.baofu.dto.request.LotteryDetailUpdateRequest;
import com.zaori.baofu.dto.request.LotteryListRequest;
import com.zaori.baofu.dto.response.DailyRecordResponse;
import com.zaori.baofu.dto.response.DailyAggregationResponse;
import com.zaori.baofu.dto.response.LotteryDetailAddResponse;
import com.zaori.baofu.dto.response.LotteryListItem;
import com.zaori.baofu.dto.response.LotteryListResponse;
import com.zaori.baofu.common.BusinessException;
import com.zaori.baofu.common.ProfitStatus;
import com.zaori.baofu.entity.LotteryDetail;
import com.zaori.baofu.entity.LotteryRecord;
import com.zaori.baofu.entity.LotteryType;
import com.zaori.baofu.mapper.LotteryDetailMapper;
import com.zaori.baofu.mapper.LotteryRecordMapper;
import com.zaori.baofu.mapper.LotteryTypeMapper;
import com.zaori.baofu.service.LotteryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 彩票记录服务实现（核心）
 *
 * 实时聚合策略（对应文档：2.业务流设计.md 实时更新策略）：
 * - 明细变更时，在同一事务内实时更新主表聚合值
 * - 投入 invest_amount = SUM(ticket_amount)
 * - 收益 profit_amount = SUM(win_amount)
 * - 净额 net_amount = profit_amount - invest_amount
 * - 收益率 profit_rate = net_amount / invest_amount * 100%（invest_amount=0 时记 0）
 * - 状态 status = 1/0/-1 由 net_amount 符号判定
 */
@Service
@RequiredArgsConstructor
public class LotteryServiceImpl implements LotteryService {

    private final LotteryRecordMapper lotteryRecordMapper;
    private final LotteryDetailMapper lotteryDetailMapper;
    private final LotteryTypeMapper lotteryTypeMapper;

    /**
     * 新增彩票明细
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LotteryDetailAddResponse addDetail(Long userId, LotteryDetailAddRequest request) {
        LocalDate date = request.getDate();

        // 1) 取或建主记录
        LotteryRecord record = lotteryRecordMapper.selectByUserDate(userId, date);
        boolean isNewRecord = (record == null);
        if (isNewRecord) {
            record = new LotteryRecord();
            record.setUserId(userId);
            record.setRecordDate(date);
            record.setInvestAmount(BigDecimal.ZERO);
            record.setProfitAmount(BigDecimal.ZERO);
            record.setNetAmount(BigDecimal.ZERO);
            record.setProfitRate(BigDecimal.ZERO);
            record.setStatus(0);
            lotteryRecordMapper.insert(record);
        }

        // 2) 写明细
        LotteryDetail detail = new LotteryDetail();
        detail.setRecordId(record.getId());
        detail.setUserId(userId);
        detail.setLotteryTypeId(request.getLotteryTypeId());
        detail.setTicketAmount(request.getTicketAmount());
        detail.setWinAmount(request.getWinAmount());
        BigDecimal netAmount = request.getWinAmount().subtract(request.getTicketAmount());
        detail.setNetAmount(netAmount);
        detail.setRemark(request.getRemark());
        lotteryDetailMapper.insert(detail);

        // 3) 实时聚合主记录
        aggregateRecord(record);

        // 4) 组装返回
        return buildAddResponse(detail, record);
    }

    /**
     * 查询每日记录
     */
    @Override
    public DailyRecordResponse getDailyDetail(Long userId, LocalDate date) {
        LotteryRecord record = lotteryRecordMapper.selectByUserDate(userId, date);
        if (record == null) {
            return null;
        }

        List<LotteryDetail> details = lotteryDetailMapper.listByRecordId(record.getId());

        DailyRecordResponse resp = new DailyRecordResponse();
        resp.setDate(record.getRecordDate());
        resp.setRecordId(record.getId());
        resp.setInvestAmount(record.getInvestAmount());
        resp.setProfitAmount(record.getProfitAmount());
        resp.setNetAmount(record.getNetAmount());
        resp.setProfitRate(record.getProfitRate() + "%");
        resp.setStatus(ProfitStatus.dbToApi(record.getStatus()));
        resp.setDetailCount(details.size());
        resp.setDetails(details.stream().map(this::toDetailItem).collect(Collectors.toList()));
        return resp;
    }

    /**
     * 历史记录列表
     * 返回结构：{total, list}
     */
    @Override
    public LotteryListResponse listRecords(Long userId, LotteryListRequest request) {
        LambdaQueryWrapper<LotteryRecord> wrapper = new LambdaQueryWrapper<LotteryRecord>()
                .eq(LotteryRecord::getUserId, userId);
        if (request.getStartDate() != null) {
            wrapper.ge(LotteryRecord::getRecordDate, request.getStartDate());
        }
        if (request.getEndDate() != null) {
            wrapper.le(LotteryRecord::getRecordDate, request.getEndDate());
        }
        wrapper.orderByDesc(LotteryRecord::getRecordDate);

        IPage<LotteryRecord> pageResult = lotteryRecordMapper.selectPage(
                new Page<>(request.getPage(), request.getSize()),
                wrapper);

        LotteryListResponse resp = new LotteryListResponse();
        resp.setTotal(pageResult.getTotal());
        resp.setList(pageResult.getRecords().stream().map(record -> {
            LotteryListItem item = new LotteryListItem();
            item.setRecordId(record.getId());
            item.setDate(record.getRecordDate());
            item.setInvestAmount(record.getInvestAmount());
            item.setProfitAmount(record.getProfitAmount());
            item.setNetAmount(record.getNetAmount());
            item.setProfitRate(record.getProfitRate() + "%");
            item.setStatus(ProfitStatus.dbToApi(record.getStatus()));
            item.setDetailCount(lotteryDetailMapper.countByRecordId(record.getId()));
            return item;
        }).collect(Collectors.toList()));
        return resp;
    }

    /**
     * 修改明细 + 实时聚合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LotteryDetailAddResponse updateDetail(Long userId, Long detailId, LotteryDetailUpdateRequest request) {
        LotteryDetail detail = lotteryDetailMapper.selectById(detailId);
        if (detail == null || !detail.getUserId().equals(userId)) {
            throw new RuntimeException("明细不存在或无权限");
        }

        if (request.getLotteryTypeId() != null) detail.setLotteryTypeId(request.getLotteryTypeId());
        if (request.getTicketAmount() != null) detail.setTicketAmount(request.getTicketAmount());
        if (request.getWinAmount() != null) detail.setWinAmount(request.getWinAmount());
        if (request.getRemark() != null) detail.setRemark(request.getRemark());

        BigDecimal netAmount = detail.getWinAmount().subtract(detail.getTicketAmount());
        detail.setNetAmount(netAmount);
        lotteryDetailMapper.updateById(detail);

        LotteryRecord record = lotteryRecordMapper.selectById(detail.getRecordId());
        if (record == null) {
            throw new BusinessException("主记录不存在");
        }
        aggregateRecord(record);

        return buildAddResponse(detail, record);
    }

    /**
     * 删除明细 + 实时聚合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDetail(Long userId, Long detailId) {
        LotteryDetail detail = lotteryDetailMapper.selectById(detailId);
        if (detail == null || !detail.getUserId().equals(userId)) {
            throw new RuntimeException("明细不存在或无权限");
        }

        Long recordId = detail.getRecordId();
        lotteryDetailMapper.deleteById(detailId);

        int remaining = lotteryDetailMapper.countByRecordId(recordId);
        if (remaining == 0) {
            lotteryRecordMapper.deleteById(recordId);
        } else {
            LotteryRecord record = lotteryRecordMapper.selectById(recordId);
            aggregateRecord(record);
        }
    }

    /**
     * 清空当日全部明细 + 删主记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearDaily(Long userId, LocalDate date) {
        LotteryRecord record = lotteryRecordMapper.selectByUserDate(userId, date);
        if (record == null) {
            return;
        }
        // 删全部明细
        java.util.List<LotteryDetail> details = lotteryDetailMapper.listByRecordId(record.getId());
        for (LotteryDetail d : details) {
            lotteryDetailMapper.deleteById(d.getId());
        }
        // 删主记录
        lotteryRecordMapper.deleteById(record.getId());
    }

    /**
     * 实时聚合：根据明细列表重算主记录聚合值
     */
    private void aggregateRecord(LotteryRecord record) {
        java.util.List<LotteryDetail> details = lotteryDetailMapper.listByRecordId(record.getId());

        BigDecimal invest = BigDecimal.ZERO;
        BigDecimal profit = BigDecimal.ZERO;
        for (LotteryDetail d : details) {
            invest = invest.add(d.getTicketAmount());
            profit = profit.add(d.getWinAmount());
        }
        BigDecimal net = profit.subtract(invest);
        BigDecimal rate = invest.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : net.multiply(new BigDecimal("100")).divide(invest, 2, RoundingMode.HALF_UP);

        record.setInvestAmount(invest);
        record.setProfitAmount(profit);
        record.setNetAmount(net);
        record.setProfitRate(rate);
        record.setStatus(Integer.compare(net.compareTo(BigDecimal.ZERO), 0));

        lotteryRecordMapper.updateById(record);
    }

    private DailyRecordResponse.DetailItem toDetailItem(LotteryDetail detail) {
        DailyRecordResponse.DetailItem item = new DailyRecordResponse.DetailItem();
        item.setDetailId(detail.getId());
        item.setLotteryTypeId(detail.getLotteryTypeId());
        LotteryType type = lotteryTypeMapper.selectById(detail.getLotteryTypeId());
        item.setLotteryTypeName(type == null ? "未知类型" : type.getName());
        item.setTicketAmount(detail.getTicketAmount());
        item.setWinAmount(detail.getWinAmount());
        item.setNetAmount(detail.getNetAmount());
        item.setRemark(detail.getRemark());
        return item;
    }

    private LotteryDetailAddResponse buildAddResponse(LotteryDetail detail, LotteryRecord record) {
        LotteryDetailAddResponse resp = new LotteryDetailAddResponse();
        resp.setDetailId(detail.getId());
        resp.setRecordId(detail.getRecordId());
        resp.setTicketAmount(detail.getTicketAmount());
        resp.setWinAmount(detail.getWinAmount());
        resp.setNetAmount(detail.getNetAmount());
        resp.setStatus(com.zaori.baofu.common.ProfitStatus.dbToApi(
                com.zaori.baofu.common.ProfitStatus.fromNetAmount(detail.getNetAmount())));

        DailyAggregationResponse agg = new DailyAggregationResponse();
        agg.setInvestAmount(record.getInvestAmount());
        agg.setProfitAmount(record.getProfitAmount());
        agg.setNetAmount(record.getNetAmount());
        agg.setProfitRate(record.getProfitRate() + "%");
        agg.setStatus(com.zaori.baofu.common.ProfitStatus.dbToApi(record.getStatus()));
        resp.setDailyAggregation(agg);
        return resp;
    }
}
