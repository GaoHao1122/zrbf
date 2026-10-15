package com.zaori.baofu.service.impl;

import com.zaori.baofu.dto.request.ChartTrendRequest;
import com.zaori.baofu.dto.response.TrendPoint;
import com.zaori.baofu.entity.LotteryRecord;
import com.zaori.baofu.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final StatisticsAggregator aggregator;

    @Override
    public List<TrendPoint> getTrend(Long userId, ChartTrendRequest request) {
        String type = request.getType() == null ? "week" : request.getType();
        LocalDate today = LocalDate.now();

        switch (type) {
            case "day": {
                LotteryRecord record = aggregator.queryRange(userId, today, today).stream().findFirst().orElse(null);
                TrendPoint point = new TrendPoint();
                point.setX(today.toString());
                point.setY(record == null ? BigDecimal.ZERO : nz(record.getNetAmount()));
                return List.of(point);
            }
            case "month": {
                YearMonth ym = YearMonth.now();
                LocalDate start = ym.atDay(1);
                LocalDate end = ym.atEndOfMonth();
                List<LotteryRecord> records = aggregator.queryRange(userId, start, end);
                return aggregator.fillDailyPoints(start, end, records, false);
            }
            case "year": {
                int year = today.getYear();
                LocalDate start = LocalDate.of(year, 1, 1);
                LocalDate end = LocalDate.of(year, 12, 31);
                List<LotteryRecord> records = aggregator.queryRange(userId, start, end);
                return aggregator.fillMonthlyPoints(year, records, true);
            }
            default: {
                LocalDate start = today.minusDays(6);
                List<LotteryRecord> records = aggregator.queryRange(userId, start, today);
                return aggregator.fillDailyPoints(start, today, records, false);
            }
        }
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
