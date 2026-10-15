// 用户状态：token、用户信息、登录/退出/获取资料

import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { getUserProfileApi, wxLoginApi } from '@/api/user';
import type { LoginResponse, UserInfo, UserProfileResponse } from '@/types/api';
import { clearAuth, getToken, getUser, setToken, setUser } from '@/utils/auth';

// 模块级登录 Promise：多个页面同时触发登录时只发一次请求
let pendingLogin: Promise<LoginResponse> | null = null;

export const useUserStore = defineStore('user', () => {
  // 从本地缓存恢复登录态
  const token = ref<string>(getToken());
  const userInfo = ref<UserInfo | null>(getUser());
  const profile = ref<UserProfileResponse | null>(null);

  // 有 token 就认为已登录
  const isLogged = computed(() => !!token.value);

  // 微信静默登录：uni.login 拿 code，再交给后端换 openid 和 token
  const login = (): Promise<LoginResponse> => {
    if (pendingLogin) return pendingLogin;
    pendingLogin = doLogin().finally(() => {
      pendingLogin = null;
    });
    return pendingLogin;
  };

  const doLogin = async (): Promise<LoginResponse> => {
    const loginResult = await new Promise<{ code: string }>((resolve, reject) => {
      uni.login({
        provider: 'weixin',
        success: (res) => resolve(res as { code: string }),
        fail: reject,
      });
    });
    const code = loginResult.code;
    if (!code) throw new Error('微信 code 获取失败');

    const res = await wxLoginApi({ code });
    token.value = res.token;
    userInfo.value = res.user;
    setToken(res.token);
    setUser(res.user);
    return res;
  };

  // 获取用户资料与累计统计（我的页使用）
  const fetchProfile = async () => {
    profile.value = await getUserProfileApi();
    return profile.value;
  };

  // 退出登录：清空内存与本地缓存
  const logout = () => {
    pendingLogin = null;
    token.value = '';
    userInfo.value = null;
    profile.value = null;
    clearAuth();
  };

  return { token, userInfo, profile, isLogged, login, fetchProfile, logout };
});
