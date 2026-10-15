// 网络请求封装：所有接口都走这里
// 统一处理：BaseURL、Token 头、加载提示、401 登录过期、错误 toast

import { getToken, clearAuth } from '@/utils/auth';

// 后端地址来自 .env 文件
const BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

export interface RequestOptions<D = unknown> {
  url: string;
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
  data?: D;
  params?: Record<string, unknown>;
  showLoading?: boolean;
  loadingText?: string;
  showError?: boolean;
}

const request = <T = unknown, D = unknown>(options: RequestOptions<D>): Promise<T> => {
  const {
    url,
    method = 'GET',
    data,
    params,
    showLoading = false,
    loadingText = '加载中',
    showError = true,
  } = options;

  if (showLoading) {
    uni.showLoading({ title: loadingText, mask: true });
  }

  // 拼接 GET 查询参数：?page=1&size=20
  let fullUrl = BASE_URL + url;
  if (params) {
    const query = Object.entries(params)
      .filter(([, value]) => value !== undefined && value !== null)
      .map(([key, value]) => `${key}=${encodeURIComponent(String(value))}`)
      .join('&');
    if (query) fullUrl += `?${query}`;
  }

  return new Promise<T>((resolve, reject) => {
    uni.request({
      url: fullUrl,
      method: method as any,
      data: data as any,
      header: {
        'Content-Type': 'application/json',
        Authorization: getToken() ? `Bearer ${getToken()}` : '',
      },
      timeout: 10000,
      success: (res) => {
        const body = res.data as { code: number; message: string; data: T };
        if (body.code === 200) {
          resolve(body.data);
        } else if (body.code === 401) {
          // 登录过期：清空本地登录态；只有之前有 token 才回首页，避免退出登录后死循环
          const hadToken = !!getToken();
          clearAuth();
          if (hadToken) {
            uni.showToast({ title: '登录已过期，请重新进入', icon: 'none' });
            uni.reLaunch({ url: '/pages/dashboard/index' });
          }
          reject(new Error('登录已过期'));
        } else {
          if (showError) {
            uni.showToast({ title: body.message || '请求失败', icon: 'none' });
          }
          reject(new Error(body.message));
        }
      },
      fail: (err) => {
        if (showError) {
          uni.showToast({ title: '网络异常，请稍后重试', icon: 'none' });
        }
        reject(err);
      },
      complete: () => {
        if (showLoading) uni.hideLoading();
      },
    });
  });
};

export default request;
