// Token 与用户信息本地缓存管理
// 小程序没有 cookie，所以登录后把 token 存在本地 Storage

import type { UserInfo } from '@/types/api';

const TOKEN_KEY = 'zrbf_token';
const USER_KEY = 'zrbf_user';

// 读取 token（没有则返回空字符串）
export const getToken = (): string => {
  return uni.getStorageSync(TOKEN_KEY) || '';
};

export const setToken = (token: string): void => {
  uni.setStorageSync(TOKEN_KEY, token);
};

// 读取本地缓存的用户信息
export const getUser = (): UserInfo | null => {
  return uni.getStorageSync(USER_KEY) || null;
};

export const setUser = (user: UserInfo): void => {
  uni.setStorageSync(USER_KEY, user);
};

// 退出登录 / 登录过期时清空本地登录态
export const clearAuth = (): void => {
  uni.removeStorageSync(TOKEN_KEY);
  uni.removeStorageSync(USER_KEY);
};
