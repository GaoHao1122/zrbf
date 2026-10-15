// 统计模块接口：今日 / 周 / 月 / 年

import request from './request';
import type {
  MonthStatisticsResponse,
  TodayStatisticsResponse,
  WeekStatisticsResponse,
  YearStatisticsResponse,
} from '@/types/api';

export const getTodayStatisticsApi = () =>
  request<TodayStatisticsResponse>({ url: '/api/v1/statistics/today' });

export const getWeekStatisticsApi = () =>
  request<WeekStatisticsResponse>({ url: '/api/v1/statistics/week' });

export const getMonthStatisticsApi = (year?: number, month?: number) =>
  request<MonthStatisticsResponse>({
    url: '/api/v1/statistics/month',
    params: { year, month },
  });

export const getYearStatisticsApi = (year?: number) =>
  request<YearStatisticsResponse>({
    url: '/api/v1/statistics/year',
    params: { year },
  });
