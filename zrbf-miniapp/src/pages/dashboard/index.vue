<template>
  <view class="dashboard">
    <!-- 首次加载显示骨架屏，避免白屏 -->
    <Skeleton v-if="loading && !loaded" :rows="4" />

    <template v-else>
      <!-- 今日盈亏主卡 -->
      <view class="dashboard__hero">
        <view class="dashboard__hero-top">
          <text class="dashboard__hero-label">今日盈亏</text>
          <StatusBadge :status="today.status" />
        </view>
        <text class="dashboard__hero-amount" :class="amountClass">
          {{ formatSignedMoney(today.netAmount) }}
        </text>
        <text class="dashboard__hero-rate">收益率 {{ formatPercent(today.profitRate) }}</text>
      </view>

      <!-- 今日分项 -->
      <view class="dashboard__row">
        <StatCard label="今日投入" :value="`¥${formatMoney(today.investAmount)}`" />
        <StatCard label="今日收益率" :value="formatPercent(today.profitRate)" />
      </view>

      <!-- 累计统计 -->
      <view class="dashboard__section">
        <text class="dashboard__section-title">累计统计</text>
        <view class="dashboard__row">
          <StatCard label="累计投入" :value="`¥${formatMoney(summary.totalInvestAmount)}`" />
          <StatCard label="累计收益" :value="`¥${formatMoney(summary.totalProfitAmount)}`" />
        </view>
        <view class="dashboard__row">
          <StatCard label="累计盈亏" :value="formatSignedMoney(summary.totalNetAmount)" :trend="summaryTrend" />
          <StatCard label="胜率" :value="formatPercent(summary.winRate)" />
        </view>
      </view>

      <!-- 本周趋势预览 -->
      <view class="dashboard__section">
        <view class="dashboard__section-head">
          <text class="dashboard__section-title">本周趋势</text>
          <text class="dashboard__section-link" @tap="goTrend">查看详情</text>
        </view>
        <view class="dashboard__chart-card">
          <TrendChart :data="chart.data" :type="chart.type" />
        </view>
      </view>
    </template>

    <!-- 空状态：今天还没登记 -->
    <EmptyState
      v-if="loaded && !hasTodayRecord"
      title="今日还没有登记哦"
      description="记录每一笔投入与收益"
      actionText="去登记"
      @action="goAdd"
    />

    <!-- 快捷操作 -->
    <view class="dashboard__actions">
      <button class="btn btn--primary btn--block" @tap="goAdd">+ 今日登记</button>
      <view class="dashboard__actions-row">
        <button class="btn btn--ghost" @tap="goHistory">历史记录</button>
        <button class="btn btn--ghost" @tap="goTrend">趋势分析</button>
        <button class="btn btn--ghost" @tap="goTodayDetail">当日详情</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onPullDownRefresh, onShow } from '@dcloudio/uni-app';
import { getDashboardApi } from '@/api/dashboard';
import type { DashboardResponse } from '@/types/api';
import { formatMoney, formatPercent, formatSignedMoney, todayStr } from '@/utils/format';
import { useUserStore } from '@/stores/user';
import Skeleton from '@/components/Skeleton/Skeleton.vue';
import StatusBadge from '@/components/StatusBadge/StatusBadge.vue';
import StatCard from '@/components/StatCard/StatCard.vue';
import TrendChart from '@/components/TrendChart/TrendChart.vue';
import EmptyState from '@/components/EmptyState/EmptyState.vue';

const loading = ref(true);
const loaded = ref(false);
const userStore = useUserStore();

const today = ref<DashboardResponse['today']>({
  investAmount: 0,
  netAmount: 0,
  profitRate: '0%',
  status: 'EVEN',
});

const summary = ref<DashboardResponse['summary']>({
  totalInvestAmount: 0,
  totalProfitAmount: 0,
  totalNetAmount: 0,
  winRate: '0%',
});

const chart = ref<DashboardResponse['chart']>({ type: 'week', data: [] });

// 今日是否已有记录：金额都是 0 就当作没有登记
const hasTodayRecord = computed(() => today.value.netAmount !== 0 || today.value.investAmount !== 0);

// 主金额颜色：盈利红、亏损绿、持平灰
const amountClass = computed(() => {
  if (today.value.status === 'PROFIT') return 'is-profit';
  if (today.value.status === 'LOSS') return 'is-loss';
  return 'is-even';
});

const summaryTrend = computed<'up' | 'down' | 'flat'>(() => {
  if (summary.value.totalNetAmount > 0) return 'up';
  if (summary.value.totalNetAmount < 0) return 'down';
  return 'flat';
});

// 拉取首页聚合数据
const load = async () => {
  loading.value = true;
  try {
    // 冷启动时 App 的静默登录可能还在进行，这里等待登录完成再请求
    if (!userStore.isLogged) {
      await userStore.login();
    }
    const data = await getDashboardApi();
    today.value = data.today;
    summary.value = data.summary;
    chart.value = data.chart;
    loaded.value = true;
  } finally {
    loading.value = false;
  }
};

// 每次回到首页都刷新（登记完返回时能看到新数据）
onShow(load);

// 下拉刷新
onPullDownRefresh(async () => {
  await load();
  uni.stopPullDownRefresh();
});

const goAdd = () => uni.navigateTo({ url: '/pages/lottery/add' });
const goHistory = () => uni.switchTab({ url: '/pages/lottery/list' });
const goTrend = () => uni.switchTab({ url: '/pages/chart/trend' });
const goTodayDetail = () => uni.navigateTo({ url: `/pages/lottery/detail?date=${todayStr()}` });
</script>

<style lang="scss" scoped>
.dashboard {
  padding: var(--spacing-md);

  &__hero {
    padding: var(--spacing-lg);
    background: var(--color-bg-card);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-card);
  }

  &__hero-top {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__hero-label {
    font-size: var(--font-base);
    color: var(--color-text-secondary);
  }

  &__hero-amount {
    display: block;
    margin-top: var(--spacing-md);
    font-size: var(--font-xxl);
    font-weight: var(--font-weight-semibold);
    font-family: $font-family-number;

    &.is-profit {
      color: var(--color-profit);
    }

    &.is-loss {
      color: var(--color-loss);
    }

    &.is-even {
      color: var(--color-text-primary);
    }
  }

  &__hero-rate {
    display: block;
    margin-top: var(--spacing-xs);
    font-size: var(--font-sm);
    color: var(--color-text-secondary);
  }

  &__row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: var(--spacing-md);
    margin-top: var(--spacing-md);
  }

  &__section {
    margin-top: var(--spacing-lg);
  }

  &__section-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: var(--spacing-md);
  }

  &__section-title {
    font-size: var(--font-md);
    font-weight: var(--font-weight-medium);
    color: var(--color-text-primary);
  }

  &__section-link {
    font-size: var(--font-sm);
    color: var(--color-primary);
  }

  &__chart-card {
    padding: var(--spacing-sm);
    background: var(--color-bg-card);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-card);
  }

  &__actions {
    margin-top: var(--spacing-lg);
    padding-bottom: var(--spacing-xxl);
  }

  &__actions-row {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: var(--spacing-md);
    margin-top: var(--spacing-md);
  }
}
</style>
