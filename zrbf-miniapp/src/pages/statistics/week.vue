<template>
  <view class="stat-page">
    <!-- 周统计汇总卡 -->
    <view v-if="data" class="stat-page__summary">
      <view class="stat-page__row">
        <StatCard label="本周投入" :value="`¥${formatMoney(data.totalInvestAmount)}`" />
        <StatCard label="本周收益" :value="`¥${formatMoney(data.totalProfitAmount)}`" />
      </view>
      <view class="stat-page__row">
        <StatCard
          label="本周净收益"
          :value="formatSignedMoney(data.totalNetAmount)"
          :trend="trendOf(data.totalNetAmount)"
        />
        <StatCard label="有记录天数" :value="`${points.length} 天`" />
      </view>
    </view>

    <!-- 周趋势图 -->
    <view class="stat-page__chart">
      <TrendChart :data="points" type="week" />
    </view>

    <!-- 每日明细 -->
    <view class="stat-page__list">
      <view v-for="p in points" :key="p.x" class="stat-page__item">
        <text class="stat-page__item-date">{{ p.x }}</text>
        <text class="stat-page__item-amount" :class="p.y >= 0 ? 'is-profit' : 'is-loss'">
          {{ formatSignedMoney(p.y) }}
        </text>
      </view>
      <EmptyState v-if="!points.length" title="暂无周统计" />
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getWeekStatisticsApi } from '@/api/statistics';
import type { TrendPoint, WeekStatisticsResponse } from '@/types/api';
import { formatMoney, formatSignedMoney } from '@/utils/format';
import StatCard from '@/components/StatCard/StatCard.vue';
import TrendChart from '@/components/TrendChart/TrendChart.vue';
import EmptyState from '@/components/EmptyState/EmptyState.vue';

const data = ref<WeekStatisticsResponse | null>(null);

// 把后端 { date, netAmount } 转成图表需要的 { x, y }
const points = computed<TrendPoint[]>(() =>
  (data.value?.chart || []).map((p) => ({ x: p.date || '', y: Number(p.netAmount) || 0 })),
);

const trendOf = (amount: number): 'up' | 'down' | 'flat' => {
  if (amount > 0) return 'up';
  if (amount < 0) return 'down';
  return 'flat';
};

const load = async () => {
  try {
    data.value = await getWeekStatisticsApi();
  } catch (err) {
    data.value = null;
  }
};

onShow(load);
</script>

<style lang="scss" scoped>
.stat-page {
  padding: var(--spacing-md);

  &__row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-md);
  }

  &__chart {
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
