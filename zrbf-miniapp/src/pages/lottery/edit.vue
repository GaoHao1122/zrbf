<template>
  <view class="edit-page">
    <view class="form-card">
      <view class="form-item">
        <text class="form-item__label">彩票类型</text>
        <picker :range="typeNames" :value="typeIndex" @change="onTypeChange">
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

    <view class="preview-card">
      <text class="preview-card__label">修改后盈亏</text>
      <text class="preview-card__amount" :class="previewClass">
        {{ formatSignedMoney(previewNet) }}
      </text>
    </view>

    <button class="btn btn--primary btn--block edit-page__submit" :disabled="submitting" @tap="handleSubmit">
      {{ submitting ? '保存中...' : '保存修改' }}
    </button>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getDailyRecordApi, updateLotteryDetailApi } from '@/api/lottery';
import { LOTTERY_TYPE_OPTIONS } from '@/utils/constants';
import { formatSignedMoney } from '@/utils/format';
import { validateAmount, validateRemark } from '@/utils/validate';
import MoneyInput from '@/components/MoneyInput/MoneyInput.vue';

const detailId = ref<number>(0);
const date = ref('');
const submitting = ref(false);

interface EditForm {
  lotteryTypeId: number;
  ticketAmount: number;
  winAmount: number;
  remark: string;
}

// 编辑表单：初始值由原明细带出
const form = ref<EditForm>({
  lotteryTypeId: LOTTERY_TYPE_OPTIONS[0].id,
  ticketAmount: 0,
  winAmount: 0,
  remark: '',
});

const typeNames = LOTTERY_TYPE_OPTIONS.map((item) => item.name);
const typeIndex = computed(() =>
  Math.max(
    0,
    LOTTERY_TYPE_OPTIONS.findIndex((item) => item.id === form.value.lotteryTypeId),
  ),
);
const currentTypeName = computed(
  () => LOTTERY_TYPE_OPTIONS.find((item) => item.id === form.value.lotteryTypeId)?.name || '请选择',
);

const previewNet = computed(() => form.value.winAmount - form.value.ticketAmount);
const previewClass = computed(() => {
  if (previewNet.value > 0) return 'is-profit';
  if (previewNet.value < 0) return 'is-loss';
  return 'is-even';
});

// 进入页面时用当天详情接口找到要编辑的明细
onLoad(async (options) => {
  detailId.value = Number(options?.detailId || 0);
  date.value = options?.date || '';
  if (!detailId.value) {
    uni.showToast({ title: '参数错误', icon: 'none' });
    setTimeout(() => uni.navigateBack(), 800);
    return;
  }
  // 日期格式不合法时直接返回，避免请求非法 URL
  if (!/^\d{4}-\d{2}-\d{2}$/.test(date.value)) {
    uni.showToast({ title: '日期参数错误', icon: 'none' });
    setTimeout(() => uni.navigateBack(), 800);
    return;
  }

  try {
    const daily = await getDailyRecordApi(date.value);
    const target = daily.details.find((item) => item.detailId === detailId.value);
    if (!target) {
      throw new Error('明细不存在');
    }
    form.value = {
      lotteryTypeId: target.lotteryTypeId,
      ticketAmount: target.ticketAmount,
      winAmount: target.winAmount,
      remark: target.remark,
    };
  } catch (err) {
    uni.showToast({ title: '加载明细失败', icon: 'none' });
    setTimeout(() => uni.navigateBack(), 800);
  }
});

const onTypeChange = (e: any) => {
  const index = Number(e.detail.value);
  form.value.lotteryTypeId = LOTTERY_TYPE_OPTIONS[index].id;
};

const handleSubmit = async () => {
  const ticketErr = validateAmount(form.value.ticketAmount);
  if (ticketErr) return uni.showToast({ title: ticketErr, icon: 'none' });

  const winErr = validateAmount(form.value.winAmount);
  if (winErr) return uni.showToast({ title: winErr, icon: 'none' });

  const remarkErr = validateRemark(form.value.remark);
  if (remarkErr) return uni.showToast({ title: remarkErr, icon: 'none' });

  submitting.value = true;
  uni.showLoading({ title: '保存中', mask: true });
  try {
    await updateLotteryDetailApi(detailId.value, form.value);
    uni.hideLoading();
    uni.showToast({ title: '修改成功', icon: 'success' });
    uni.vibrateShort();
    setTimeout(() => uni.navigateBack(), 800);
  } catch (err) {
    uni.hideLoading();
  } finally {
    submitting.value = false;
  }
};
</script>

<style lang="scss" scoped>
.edit-page {
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
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: var(--spacing-md);

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
