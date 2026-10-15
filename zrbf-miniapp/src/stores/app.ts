// 全局应用状态：目前只放一个全局加载标记，后续可扩展主题、刷新信号等

import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useAppStore = defineStore('app', () => {
  const globalLoading = ref(false);

  const setGlobalLoading = (loading: boolean) => {
    globalLoading.value = loading;
  };

  return { globalLoading, setGlobalLoading };
});
