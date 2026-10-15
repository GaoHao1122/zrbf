// 图表数据接口

import request from './request';
import type { TrendPoint, TrendType } from '@/types/api';

// GET /api/v1/chart/trend?type=week|month|year
export const getTrendApi = (type: TrendType) =>
  request<TrendPoint[]>({ url: '/api/v1/chart/trend', params: { type } });
