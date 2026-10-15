<template>
  <view class="status-badge" :class="[`status-badge--${normalized}`, `status-badge--${size}`]">
    <text class="status-badge__text">{{ meta.label }}</text>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { PROFIT_STATUS_META } from '@/utils/constants';
import type { ProfitStatus } from '@/types/api';

// status 为空时表示“今日未登记”
const props = withDefaults(
  defineProps<{
    status?: ProfitStatus | '';
    size?: 'sm' | 'md';
  }>(),
  { status: '', size: 'md' },
);

const normalized = computed(() => (props.status ? props.status.toLowerCase() : 'even'));

// 取状态对应的文案与颜色；未登记给一个灰色标签
const meta = computed(() => {
  if (!props.status) {
    return { label: '未登记', color: 'var(--color-text-secondary)', bg: '#f5f5f5' };
  }
  return PROFIT_STATUS_META[props.status];
});
</script>

<style lang="scss" scoped>
.status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);

  &__text {
    font-size: var(--font-xs);
    line-height: 1;
  }

  &--profit {
    background: #fff1f0;
    color: var(--color-profit);
  }

  &--even {
    background: #f5f5f5;
    color: var(--color-even);
  }

  &--loss {
    background: #f6ffed;
    color: var(--color-loss);
  }

  &--sm {
    height: 32rpx;
    padding: 0 var(--spacing-xs);
    border-radius: var(--radius-sm);
  }

  &--md {
    height: 40rpx;
    padding: 0 var(--spacing-sm);
    border-radius: var(--radius-md);
  }
}
</style>
