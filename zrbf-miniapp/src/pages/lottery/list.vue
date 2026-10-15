<template>
  <view class="list-page">
    <!-- 日期范围筛选 -->
    <view class="filter-bar">
      <view class="filter-bar__item">
        <text class="filter-bar__label">开始</text>
        <DatePicker
          :model-value="dateRange.startDate"
          placeholder="不限"
          @update:model-value="onStartChange"
        />
      </view>
      <text class="filter-bar__separator">至</text>
      <view class="filter-bar__item">
        <text class="filter-bar__label">结束</text>
        <DatePicker
          :model-value="dateRange.endDate"
          placeholder="不限"
          @update:model-value="onEndChange"
        />
      </view>
      <button class="filter-bar__reset" @tap="resetFilter">重置</button>
    </view>

    <!-- 历史记录列表 -->
    <view v-if="store.historyList.length" class="list-page__content">
      <view
        v-for="item in store.historyList"
        :key="item.recordId"
        class="record-item"
        @tap="goDetail(item.date)"
      >
        <view class="record-item__head">
          <text class="record-item__date">{{ item.date }}</text>
          <view class="record-item__status">
            <StatusBadge :status="item.status" size="sm" />
            <text class="record-item__net" :class="netClass(item.netAmount)">
              {{ formatSignedMoney(item.netAmount) }}
            </text>
          </view>
        </view>
        <view class="record-item__meta">
          <text>投入 ¥{{ formatMoney(item.investAmount) }}</text>
          <text>收益 ¥{{ formatMoney(item.profitAmount) }}</text>
          <text>{{ item.detailCount }} 笔明细</text>
        </view>
      </view>

      <LoadMore
        :loading="loading"
        :finished="finished"
        :empty="!store.historyList.length"
      />
    </view>

    <EmptyState
      v-else-if="!loading"
      title="暂无历史记录"
      description="去记录第一笔彩票投入吧"
      actionText="去登记"
      @action="goAdd"
    />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import { useLotteryStore } from '@/stores/lottery';
import { formatMoney, formatSignedMoney } from '@/utils/format';
import StatusBadge from '@/components/StatusBadge/StatusBadge.vue';
import LoadMore from '@/components/LoadMore/LoadMore.vue';
import EmptyState from '@/components/EmptyState/EmptyState.vue';
import DatePicker from '@/components/DatePicker/DatePicker.vue';

const store = useLotteryStore();
const page = ref(1);
const size = 20;
const loading = ref(false);
const finished = ref(false);
const dateRange = ref<{ startDate?: string; endDate?: string }>({});

// 加载列表：reset=true 表示重新从第一页拉
const load = async (reset = false) => {
  if (loading.value) return;
  if (reset) {
    page.value = 1;
    finished.value = false;
    store.resetHistory();
  }
  if (finished.value) return;

  loading.value = true;
  try {
    await store.fetchHistory({ page: page.value, size, ...dateRange.value });
    if (store.historyList.length >= store.historyTotal) {
      finished.value = true;
    } else {
      page.value += 1;
    }
  } finally {
    loading.value = false;
  }
};

// 首次进入与每次回到页面都刷新
onShow(() => load(true));

// 下拉刷新
onPullDownRefresh(async () => {
  await load(true);
  uni.stopPullDownRefresh();
});

// 触底加载下一页
onReachBottom(() => load(false));

const onStartChange = (value: string) => {
  dateRange.value.startDate = value;
  load(true);
};

const onEndChange = (value: string) => {
  dateRange.value.endDate = value;
  load(true);
};

const resetFilter = () => {
  dateRange.value = {};
  load(true);
};

const netClass = (amount: number) => {
  if (amount > 0) return 'is-profit';
  if (amount < 0) return 'is-loss';
  return 'is-even';
};

const goDetail = (date: string) => uni.navigateTo({ url: `/pages/lottery/detail?date=${date}` });
const goAdd = () => uni.navigateTo({ url: '/pages/lottery/add' });
</script>

<style lang="scss" scoped>
.list-page {
  padding: var(--spacing-md);
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);

  &__item {
    display: flex;
    align-items: center;
    gap: var(--spacing-xs);
  }

  &__label {
    font-size: var(--font-xs);
    color: var(--color-text-secondary);
  }

  &__value {
    font-size: var(--font-sm);
    color: var(--color-text-primary);
  }

  &__separator {
    font-size: var(--font-sm);
    color: var(--color-text-placeholder);
  }

  &__reset {
    margin-left: auto;
    padding: 8rpx 20rpx;
    font-size: var(--font-sm);
    color: var(--color-primary);
    border: 1rpx solid var(--color-primary);
    border-radius: var(--radius-full);
  }
}

.record-item {
  margin-bottom: var(--spacing-md);
  padding: var(--spacing-md);
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
    color: var(--color-text-primary);
  }

  &__status {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
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
    gap: var(--spacing-md);
    margin-top: var(--spacing-sm);
    font-size: var(--font-sm);
    color: var(--color-text-secondary);
  }
}
</style>
