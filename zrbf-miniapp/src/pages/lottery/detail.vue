<template>
  <view class="detail-page">
    <!-- 当日聚合信息卡 -->
    <view v-if="record" class="summary-card">
      <view class="summary-card__head">
        <text class="summary-card__date">{{ record.date }}</text>
        <StatusBadge :status="record.status" />
      </view>
      <text class="summary-card__amount" :class="netClass(record.netAmount)">
        {{ formatSignedMoney(record.netAmount) }}
      </text>
      <view class="summary-card__meta">
        <text>投入 ¥{{ formatMoney(record.investAmount) }}</text>
        <text>收益 ¥{{ formatMoney(record.profitAmount) }}</text>
        <text>收益率 {{ formatPercent(record.profitRate) }}</text>
      </view>
    </view>

    <!-- 明细列表 -->
    <view class="detail-page__section">
      <view class="detail-page__section-head">
        <text class="detail-page__section-title">明细列表（{{ record?.detailCount || 0 }}）</text>
        <button class="detail-page__add" @tap="goAdd">+ 添加</button>
      </view>

      <view v-if="record?.details.length" class="detail-page__list">
        <DetailItem
          v-for="item in record.details"
          :key="item.detailId"
          :detail-id="item.detailId"
          :lottery-type-name="item.lotteryTypeName"
          :ticket-amount="item.ticketAmount"
          :win-amount="item.winAmount"
          :net-amount="item.netAmount"
          :remark="item.remark"
          :status="statusOf(item.netAmount)"
          editable
          @edit="goEdit"
          @delete="handleDelete"
        />
      </view>

      <EmptyState
        v-else-if="loaded"
        title="当日暂无明细"
        description="点击添加，记录第一笔投入"
        actionText="去添加"
        @action="goAdd"
      />
    </view>

    <button v-if="record" class="btn btn--danger btn--block detail-page__clear" @tap="handleClear">
      清空当日
    </button>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import {
  clearDailyLotteryApi,
  deleteLotteryDetailApi,
  getDailyRecordApi,
} from '@/api/lottery';
import type { DailyRecordResponse, ProfitStatus } from '@/types/api';
import { formatMoney, formatPercent, formatSignedMoney } from '@/utils/format';
import StatusBadge from '@/components/StatusBadge/StatusBadge.vue';
import DetailItem from '@/components/DetailItem/DetailItem.vue';
import EmptyState from '@/components/EmptyState/EmptyState.vue';

const date = ref('');
const record = ref<DailyRecordResponse | null>(null);
const loaded = ref(false);

// 页面参数：?date=2026-08-11
onLoad((options) => {
  const targetDate = options?.date || '';
  // 日期格式不合法时直接返回，避免请求非法 URL
  if (!/^\d{4}-\d{2}-\d{2}$/.test(targetDate)) {
    uni.showToast({ title: '日期参数错误', icon: 'none' });
    setTimeout(() => uni.navigateBack(), 800);
    return;
  }
  date.value = targetDate;
  reload();
});

// 拉取某一天的主记录与明细
const reload = async () => {
  loaded.value = false;
  try {
    record.value = await getDailyRecordApi(date.value);
  } catch (err) {
    // 404 说明当天没有记录，保留空状态
    record.value = null;
  } finally {
    loaded.value = true;
  }
};

const statusOf = (netAmount: number): ProfitStatus => {
  if (netAmount > 0) return 'PROFIT';
  if (netAmount < 0) return 'LOSS';
  return 'EVEN';
};

const netClass = (amount: number) => {
  if (amount > 0) return 'is-profit';
  if (amount < 0) return 'is-loss';
  return 'is-even';
};

// 删除单条明细：必须二次确认
const handleDelete = (detailId: number) => {
  uni.showModal({
    title: '确认删除',
    content: '确定删除该笔明细？',
    confirmColor: '#FF4D4F',
    success: async (res) => {
      if (res.confirm) {
        await deleteLotteryDetailApi(detailId);
        uni.showToast({ title: '删除成功', icon: 'success' });
        uni.vibrateShort();
        await reload();
      }
    },
  });
};

// 清空当日全部明细：危险操作，必须二次确认
const handleClear = () => {
  uni.showModal({
    title: '清空当日',
    content: '确定清空当日全部明细？此操作不可恢复',
    confirmColor: '#FF4D4F',
    success: async (res) => {
      if (res.confirm) {
        await clearDailyLotteryApi(date.value);
        uni.showToast({ title: '已清空', icon: 'success' });
        uni.vibrateShort();
        uni.navigateBack();
      }
    },
  });
};

const goAdd = () => {
  uni.navigateTo({ url: `/pages/lottery/add?date=${date.value}` });
};

const goEdit = (detailId: number) => {
  uni.navigateTo({ url: `/pages/lottery/edit?detailId=${detailId}&date=${date.value}` });
};
</script>

<style lang="scss" scoped>
.detail-page {
  padding: var(--spacing-md);

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
  }

  &__add {
    padding: 8rpx 24rpx;
    font-size: var(--font-sm);
    color: var(--color-primary);
    background: var(--color-primary-light);
    border-radius: var(--radius-full);
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-md);
  }

  &__clear {
    margin-top: var(--spacing-xxl);
  }
}

.summary-card {
  padding: var(--spacing-lg);
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);

  &__head {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__date {
    font-size: var(--font-base);
    font-weight: var(--font-weight-medium);
  }

  &__amount {
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
      color: var(--color-even);
    }
  }

  &__meta {
    display: flex;
    flex-wrap: wrap;
    gap: var(--spacing-md);
    margin-top: var(--spacing-md);
    font-size: var(--font-sm);
    color: var(--color-text-secondary);
  }
}
</style>
