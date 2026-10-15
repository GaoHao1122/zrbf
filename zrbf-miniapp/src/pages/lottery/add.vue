<template>
  <view class="add-page">
    <!-- 表单卡片 -->
    <view class="form-card">
      <view class="form-item">
        <text class="form-item__label">日期</text>
        <DatePicker v-model="form.date" :end="today" placeholder="请选择日期" />
      </view>

      <view class="form-item">
        <text class="form-item__label">彩票类型</text>
        <picker :range="typeNames" @change="onTypeChange">
          <view class="form-item__control">
            <text>{{ currentTypeName }}</text>
            <text class="form-item__arrow">›</text>
          </view>
        </picker>
      </view>

      <view class="form-item">
        <text class="form-item__label">投入本金（元）</text>
        <MoneyInput v-model="form.ticketAmount" placeholder="0.00" />
      </view>

      <view class="form-item">
        <text class="form-item__label">中奖金额（元）</text>
        <MoneyInput v-model="form.winAmount" placeholder="0.00" />
      </view>

      <view class="form-item">
        <text class="form-item__label">备注</text>
        <input
          v-model="form.remark"
          class="form-item__input"
          placeholder="选填，最多 100 字"
          maxlength="100"
        />
      </view>
    </view>

    <!-- 实时盈亏预览：netAmount = winAmount - ticketAmount -->
    <view class="preview-card">
      <text class="preview-card__label">预计盈亏</text>
      <view class="preview-card__row">
        <text class="preview-card__amount" :class="previewClass">
          {{ formatSignedMoney(previewNet) }}
        </text>
        <StatusBadge :status="previewStatus" />
      </view>
    </view>

    <button
      class="btn btn--primary btn--block add-page__submit"
      :disabled="submitting"
      @tap="handleSubmit"
    >
      {{ submitting ? '保存中...' : '保存记录' }}
    </button>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { addLotteryDetail } from '@/api/lottery';
import type { LotteryForm } from '@/types/lottery';
import { LOTTERY_TYPE_OPTIONS } from '@/utils/constants';
import { formatSignedMoney, todayStr } from '@/utils/format';
import { validateAmount, validateDate, validateRemark } from '@/utils/validate';
import MoneyInput from '@/components/MoneyInput/MoneyInput.vue';
import StatusBadge from '@/components/StatusBadge/StatusBadge.vue';
import DatePicker from '@/components/DatePicker/DatePicker.vue';

// 用本地时区的今天，避免 UTC 在凌晨显示成昨天
const today = todayStr();

// 表单默认值：今天、大乐透、金额 0
const form = ref<LotteryForm>({
  date: today,
  lotteryTypeId: LOTTERY_TYPE_OPTIONS[0].id,
  ticketAmount: 0,
  winAmount: 0,
  remark: '',
});

// 如果从“当日详情”页跳转过来，自动带上对应的日期
onLoad((options) => {
  if (options?.date && /^\d{4}-\d{2}-\d{2}$/.test(options.date)) {
    form.value.date = options.date;
  }
});

const submitting = ref(false);

// picker 展示用的彩票类型名称
const typeNames = LOTTERY_TYPE_OPTIONS.map((item) => item.name);
const currentTypeName = computed(
  () => LOTTERY_TYPE_OPTIONS.find((item) => item.id === form.value.lotteryTypeId)?.name || '请选择',
);

// 实时预览净盈亏
const previewNet = computed(() => form.value.winAmount - form.value.ticketAmount);
const previewStatus = computed<'PROFIT' | 'EVEN' | 'LOSS'>(() => {
  if (previewNet.value > 0) return 'PROFIT';
  if (previewNet.value < 0) return 'LOSS';
  return 'EVEN';
});
const previewClass = computed(() => {
  if (previewNet.value > 0) return 'is-profit';
  if (previewNet.value < 0) return 'is-loss';
  return 'is-even';
});

const onTypeChange = (e: any) => {
  const index = Number(e.detail.value);
  form.value.lotteryTypeId = LOTTERY_TYPE_OPTIONS[index].id;
};

// 提交前逐项校验，全部通过才调接口
const handleSubmit = async () => {
  const dateErr = validateDate(form.value.date);
  if (dateErr) return uni.showToast({ title: dateErr, icon: 'none' });

  const ticketErr = validateAmount(form.value.ticketAmount);
  if (ticketErr) return uni.showToast({ title: ticketErr, icon: 'none' });

  const winErr = validateAmount(form.value.winAmount);
  if (winErr) return uni.showToast({ title: winErr, icon: 'none' });

  const remarkErr = validateRemark(form.value.remark);
  if (remarkErr) return uni.showToast({ title: remarkErr, icon: 'none' });

  submitting.value = true;
  uni.showLoading({ title: '保存中', mask: true });
  try {
    await addLotteryDetail(form.value);
    uni.hideLoading();
    uni.showToast({ title: '保存成功', icon: 'success' });
    uni.vibrateShort();
    // 保存成功后返回上一页（首页 onShow 会重新拉数据）
    setTimeout(() => uni.navigateBack(), 800);
  } catch (err) {
    uni.hideLoading();
  } finally {
    submitting.value = false;
  }
};
</script>

<style lang="scss" scoped>
.add-page {
  padding: var(--spacing-md);

  &__submit {
    margin-top: var(--spacing-lg);
  }
}

.form-card,
.preview-card {
  padding: var(--spacing-md);
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}

.form-item {
  padding: var(--spacing-md) 0;
  border-bottom: 1rpx solid var(--color-border);

  &:last-child {
    border-bottom: none;
  }

  &__label {
    display: block;
    margin-bottom: var(--spacing-sm);
    font-size: var(--font-sm);
    color: var(--color-text-secondary);
  }

  &__control {
    display: flex;
    align-items: center;
    justify-content: space-between;
    min-height: 56rpx;
    font-size: var(--font-base);
    color: var(--color-text-primary);
  }

  &__arrow {
    font-size: var(--font-lg);
    color: var(--color-text-placeholder);
  }

  &__input {
    height: 72rpx;
    font-size: var(--font-base);
  }
}

.preview-card {
  margin-top: var(--spacing-md);

  &__label {
    font-size: var(--font-sm);
    color: var(--color-text-secondary);
  }

  &__row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: var(--spacing-sm);
  }

  &__amount {
    font-size: var(--font-xl);
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
}
</style>
