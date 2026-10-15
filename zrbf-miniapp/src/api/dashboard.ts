// 首页聚合接口

import request from './request';
import type { DashboardResponse } from '@/types/api';

// GET /api/v1/dashboard 首页今日数据 + 累计统计 + 趋势预览
export const getDashboardApi = () =>
  request<DashboardResponse>({ url: '/api/v1/dashboard' });
