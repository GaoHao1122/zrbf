<template>
  <view class="trend-page">
    <!-- 周 / 月 / 年切换 -->
    <view class="segmented">
      <view
        v-for="tab in TREND_TABS"
        :key="tab.type"
        class="segmented__item"
        :class="{ 'is-active': activeType === tab.type }"
        @tap="switchTab(tab.type)"
      >
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <!-- 累计盈亏摘要 -->
    <view class="trend-page__summary">
      <text class="trend-page__summary-label">累计盈亏</text>
      <text class="trend-page__summary-value" :class="totalClass">
        {{ formatSignedMoney(totalNet) }}
      </text>
      <text class="trend-page__summary-meta">{{ profitDays }} 天盈利 / {{ points.length }} 天有记录</text>
    </view>

    <!-- 趋势折线图 -->
    <view class="trend-page__chart">
      <TrendChart :data="points" :type="activeType" />
    </view>

    <!-- 每日/每月明细列表 -->
    <view class="trend-page__list">
      <view v-for="point in points" :key="point.x" class="trend-page__item">
        <text class="trend-page__item-date">{{ point.x }}</text>
        <text class="trend-page__item-amount" :class="point.y >= 0 ? 'is-profit' : 'is-loss'">
          {{ formatSignedMoney(point.y) }}
        </text>
      </view>
      <EmptyState v-if="!points.length" title="暂无趋势数据" />
    </view>

    <!-- 统计详情入口 -->
    <view class="trend-page__stats-links">
      <button class="btn btn--ghost" @tap="goWeek">周统计</button>
      <button class="btn btn--ghost" @tap="goMonth">月统计</button>
      <button class="btn btn--ghost" @tap="goYear">年统计</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onPullDownRefresh, onShow } from '@dcloudio/uni-app';
import { getTrendApi } from '@/api/chart';
import type { TrendPoint } from '@/types/api';
import { TREND_TABS, type TrendTabType } from '@/utils/constants';
import { formatSignedMoney } from '@/utils/format';
import TrendChart from '@/components/TrendChart/TrendChart.vue';
import EmptyState from '@/components/EmptyState/EmptyState.vue';

const activeType = ref<TrendTabType>('week');
const points = ref<TrendPoint[]>([]);

// 切换 Tab 时重新请求
const switchTab = (type: TrendTabType) => {
  if (activeType.value === type) return;
  activeType.value = type;
  load();
};

const load = async () => {
  try {
    points.value = await getTrendApi(activeType.value);
  } catch (err) {
    points.value = [];
  }
};

// 累计盈亏 = 所有点相加
const totalNet = computed(() => points.value.reduce((sum, p) => sum + (Number(p.y) || 0), 0));
const totalClass = computed(() => {
  if (totalNet.value > 0) return 'is-profit';
  if (totalNet.value < 0) return 'is-loss';
  return 'is-even';
});
const profitDays = computed(() => points.value.filter((p) => Number(p.y) > 0).length);

onShow(load);
onPullDownRefresh(async () => {
  await load();
  uni.stopPullDownRefresh();
});

const goWeek = () => uni.navigateTo({ url: '/pages/statistics/week' });
const goMonth = () => uni.navigateTo({ url: '/pages/statistics/month' });
const goYear = () => uni.navigateTo({ url: '/pages/statistics/year' });
</script>

<style lang="scss" scoped>
.trend-page {
  padding: var(--spacing-md);

  &__summary {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: var(--spacing-lg);
    padding: var(--spacing-lg);
    background: var(--color-bg-card);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-card);
  }

  &__summary-label {
    font-size: var(--font-sm);
    color: var(--color-text-secondary);
  }

  &__summary-value {
    margin-top: var(--spacing-sm);
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
      color: var(--color-even);
    }
  }

  &__summary-meta {
    margin-top: var(--spacing-xs);
    font-size: var(--font-xs);
    color: var(--color-text-secondary);
  }

  &__chart {
    margin-top: var(--spacing-md);
    padding: var(--spacing-sm);
    background: var(--color-bg-card);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-card);
  }

  &__list {
    margin-top: var(--spacing-lg);
  }

  &__item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: var(--spacing-md) 0;
    border-bottom: 1rpx solid var(--color-border);
  }

  &__item-date {
    font-size: var(--font-base);
    color: var(--color-text-regular);
  }

  &__item-amount {
    font-size: var(--font-base);
    font-weight: var(--font-weight-medium);
    font-family: $font-family-number;

    &.is-profit {
      color: var(--color-profit);
    }

    &.is-loss {
      color: var(--color-loss);
    }
  }

  &__stats-links {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: var(--spacing-md);
    margin-top: var(--spacing-lg);
    padding-bottom: var(--spacing-xxl);
  }
}

.segmented {
  display: flex;
  padding: 6rpx;
  background: #ececec;
  border-radius: var(--radius-full);

  &__item {
    flex: 1;
    padding: 12rpx 0;
    text-align: center;
    font-size: var(--font-base);
    color: var(--color-text-regular);
    border-radius: var(--radius-full);
    transition: all var(--duration-fast) ease-out;

    &.is-active {
      color: var(--color-primary);
      font-weight: var(--font-weight-medium);
      background: #ffffff;
      box-shadow: var(--shadow-card);
    }
  }
}
</style>
