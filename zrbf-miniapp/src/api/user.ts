// 用户模块接口

import request from './request';
import type { LoginResponse, UserProfileResponse } from '@/types/api';

// POST /api/v1/user/login 微信登录
export const wxLoginApi = (data: { code: string }) =>
  request<LoginResponse>({ url: '/api/v1/user/login', method: 'POST', data });

// GET /api/v1/user/profile 获取用户信息与累计统计
export const getUserProfileApi = () =>
  request<UserProfileResponse>({ url: '/api/v1/user/profile' });
