// 彩票记录模块接口（核心业务）

import request from './request';
import type {
  DailyRecordResponse,
  LotteryDetailAddRequest,
  LotteryDetailAddResponse,
  LotteryDetailUpdateRequest,
  LotteryListResponse,
} from '@/types/api';

// POST /api/v1/lottery 新增一笔彩票明细
export const addLotteryDetail = (data: LotteryDetailAddRequest) =>
  request<LotteryDetailAddResponse>({ url: '/api/v1/lottery', method: 'POST', data });

// GET /api/v1/lottery/detail/{date} 查询某一天的记录与明细
export const getDailyRecordApi = (date: string) =>
  request<DailyRecordResponse>({ url: `/api/v1/lottery/detail/${date}` });

// GET /api/v1/lottery/list 分页查询历史记录
export const getLotteryListApi = (params: {
  page: number;
  size: number;
  startDate?: string;
  endDate?: string;
}) => request<LotteryListResponse>({ url: '/api/v1/lottery/list', params });

// PUT /api/v1/lottery/detail/{id} 修改明细
export const updateLotteryDetailApi = (id: number, data: LotteryDetailUpdateRequest) =>
  request<void>({ url: `/api/v1/lottery/detail/${id}`, method: 'PUT', data });

// DELETE /api/v1/lottery/detail/{id} 删除单条明细
export const deleteLotteryDetailApi = (id: number) =>
  request<void>({ url: `/api/v1/lottery/detail/${id}`, method: 'DELETE' });

// DELETE /api/v1/lottery/clear/{date} 清空某一天全部明细
export const clearDailyLotteryApi = (date: string) =>
  request<void>({ url: `/api/v1/lottery/clear/${date}`, method: 'DELETE' });
