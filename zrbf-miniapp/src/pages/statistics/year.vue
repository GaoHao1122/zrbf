<template>
  <view class="stat-page">
    <!-- 年度汇总 -->
    <view v-if="data" class="stat-page__year">
      <text class="stat-page__year-label">{{ data.year }} 年度统计</text>
      <text class="stat-page__year-value" :class="totalClass">
        {{ formatSignedMoney(totalNet) }}
      </text>
      <text class="stat-page__year-meta">累计盈亏（{{ points.length }} 个月有记录）</text>
    </view>

    <!-- 年度趋势图 -->
    <view class="stat-page__chart">
      <TrendChart :data="points" type="year" />
    </view>

    <!-- 每月明细 -->
    <view class="stat-page__list">
      <view v-for="p in points" :key="p.x" class="stat-page__item">
        <text class="stat-page__item-date">{{ p.x }}</text>
        <text class="stat-page__item-amount" :class="p.y >= 0 ? 'is-profit' : 'is-loss'">
          {{ formatSignedMoney(p.y) }}
        </text>
      </view>
      <EmptyState v-if="!points.length" title="暂无年度统计" />
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getYearStatisticsApi } from '@/api/statistics';
import type { TrendPoint, YearStatisticsResponse } from '@/types/api';
import { formatSignedMoney } from '@/utils/format';
import TrendChart from '@/components/TrendChart/TrendChart.vue';
import EmptyState from '@/components/EmptyState/EmptyState.vue';

const data = ref<YearStatisticsResponse | null>(null);

// 后端年统计返回 { date, netAmount }，转成 { x: "2026-01", y }
const points = computed<TrendPoint[]>(() =>
  (data.value?.chart || []).map((p) => ({ x: p.date || '', y: Number(p.netAmount) || 0 })),
);

const totalNet = computed(() => points.value.reduce((sum, p) => sum + p.y, 0));
const totalClass = computed(() => {
  if (totalNet.value > 0) return 'is-profit';
  if (totalNet.value < 0) return 'is-loss';
  return 'is-even';
});

const load = async () => {
  const now = new Date();
  try {
    // 默认统计当前年份
    data.value = await getYearStatisticsApi(now.getFullYear());
  } catch (err) {
    data.value = null;
  }
};

onShow(load);
</script>

<style lang="scss" scoped>
.stat-page {
  padding: var(--spacing-md);

  &__year {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: var(--spacing-lg);
    background: var(--color-bg-card);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-card);
  }

  &__year-label {
    font-size: var(--font-sm);
    color: var(--color-text-secondary);
  }

  &__year-value {
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

  &__year-meta {
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
}
</style>
