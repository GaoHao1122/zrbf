<template>
  <view class="stat-card" :class="{ 'is-emphasis': emphasis }">
    <!-- 卡片标签：例如“今日投入” -->
    <text class="stat-card__label">{{ label }}</text>
    <!-- 卡片数值：金额或百分比 -->
    <text class="stat-card__value" :class="valueClass">{{ value }}</text>
    <!-- 可选趋势箭头 -->
    <text v-if="trend" class="stat-card__trend">{{ trendText }}</text>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';

// Props 说明：
// label    - 标签文本
// value    - 数值文本（调用方先格式化好）
// trend    - up/down/flat，决定数值颜色
// emphasis - 是否大字号强调
const props = withDefaults(
  defineProps<{
    label: string;
    value: string;
    trend?: 'up' | 'down' | 'flat' | null;
    emphasis?: boolean;
  }>(),
  { trend: null, emphasis: false },
);

const valueClass = computed(() => `stat-card__value--${props.trend || 'default'}`);

const trendText = computed(() => {
  if (props.trend === 'up') return '较上次上升';
  if (props.trend === 'down') return '较上次下降';
  if (props.trend === 'flat') return '与上次持平';
  return '';
});
</script>

<style lang="scss" scoped>
.stat-card {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  min-height: 160rpx;
  padding: var(--spacing-md);
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);

  &__label {
    font-size: var(--font-sm);
    color: var(--color-text-secondary);
  }

  &__value {
    font-size: var(--font-xl);
    font-weight: var(--font-weight-semibold);
    font-family: $font-family-number;
    line-height: 1.2;

    &--up {
      color: var(--color-profit);
    }

    &--down {
      color: var(--color-loss);
    }

    &--flat {
      color: var(--color-even);
    }

    &--default {
      color: var(--color-text-primary);
    }

    &.is-emphasis {
      font-size: var(--font-xxl);
    }
  }

  &__trend {
    font-size: var(--font-xs);
    color: var(--color-text-secondary);
  }

  &.is-emphasis {
    min-height: 200rpx;
  }
}
</style>
