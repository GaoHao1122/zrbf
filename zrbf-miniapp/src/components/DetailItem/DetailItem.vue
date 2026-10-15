<template>
  <view class="detail-item">
    <!-- 第一行：彩票类型 + 净盈亏 -->
    <view class="detail-item__header">
      <view class="detail-item__left">
        <text class="detail-item__name">{{ lotteryTypeName }}</text>
        <StatusBadge :status="status" size="sm" />
      </view>
      <text class="detail-item__net" :class="netClass">{{ formatSignedMoney(netAmount) }}</text>
    </view>

    <!-- 第二行：投入与收益 -->
    <view class="detail-item__meta">
      <text>投入 ¥{{ formatMoney(ticketAmount) }}</text>
      <text class="detail-item__divider">|</text>
      <text>收益 ¥{{ formatMoney(winAmount) }}</text>
    </view>

    <!-- 备注 -->
    <text v-if="remark" class="detail-item__remark">{{ remark }}</text>

    <!-- 操作按钮（列表页可不展示） -->
    <view v-if="editable" class="detail-item__actions">
      <button class="detail-item__btn" @tap="$emit('edit', detailId)">编辑</button>
      <button class="detail-item__btn detail-item__btn--danger" @tap="$emit('delete', detailId)">删除</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import StatusBadge from '@/components/StatusBadge/StatusBadge.vue';
import { formatMoney, formatSignedMoney } from '@/utils/format';
import type { ProfitStatus } from '@/types/api';

const props = withDefaults(
  defineProps<{
    detailId: number;
    lotteryTypeName: string;
    ticketAmount: number;
    winAmount: number;
    netAmount: number;
    remark?: string;
    status?: ProfitStatus;
    editable?: boolean;
  }>(),
  { remark: '', status: 'EVEN', editable: false },
);

// 根据净盈亏显示红/绿
const netClass = computed(() => {
  if (props.netAmount > 0) return 'is-profit';
  if (props.netAmount < 0) return 'is-loss';
  return 'is-even';
});

defineEmits<{
  (e: 'edit', detailId: number): void;
  (e: 'delete', detailId: number): void;
}>();
</script>

<style lang="scss" scoped>
.detail-item {
  padding: var(--spacing-md);
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__left {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
  }

  &__name {
    font-size: var(--font-base);
    font-weight: var(--font-weight-medium);
    color: var(--color-text-primary);
  }

  &__net {
    font-size: var(--font-lg);
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

  &__meta {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    margin-top: var(--spacing-sm);
    font-size: var(--font-sm);
    color: var(--color-text-secondary);
  }

  &__divider {
    color: var(--color-border);
  }

  &__remark {
    display: block;
    margin-top: var(--spacing-xs);
    font-size: var(--font-sm);
    color: var(--color-text-regular);
  }

  &__actions {
    display: flex;
    justify-content: flex-end;
    gap: var(--spacing-sm);
    margin-top: var(--spacing-md);
  }

  &__btn {
    padding: 8rpx 24rpx;
    font-size: var(--font-sm);
    color: var(--color-text-regular);
    border: 1rpx solid var(--color-border);
    border-radius: var(--radius-md);

    &--danger {
      color: var(--color-error);
      border-color: rgba(255, 77, 79, 0.4);
    }
  }
}
</style>
