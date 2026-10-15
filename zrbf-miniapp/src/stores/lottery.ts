// 彩票记录状态：今日记录与历史列表的共享数据

import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getDailyRecordApi, getLotteryListApi } from '@/api/lottery';
import type { DailyRecordResponse, LotteryListItem } from '@/types/api';

export const useLotteryStore = defineStore('lottery', () => {
  const todayRecord = ref<DailyRecordResponse | null>(null);
  const historyList = ref<LotteryListItem[]>([]);
  const historyTotal = ref(0);

  // 拉取某一天的记录
  const fetchToday = async (date: string) => {
    todayRecord.value = await getDailyRecordApi(date);
  };

  // 拉取历史列表；第一页重置，后续翻页追加
  const fetchHistory = async (params: {
    page: number;
    size: number;
    startDate?: string;
    endDate?: string;
  }) => {
    const res = await getLotteryListApi(params);
    if (params.page === 1) {
      historyList.value = res.list;
    } else {
      historyList.value.push(...res.list);
    }
    historyTotal.value = Number(res.total);
  };

  const resetHistory = () => {
    historyList.value = [];
    historyTotal.value = 0;
  };

  return { todayRecord, historyList, historyTotal, fetchToday, fetchHistory, resetHistory };
});
