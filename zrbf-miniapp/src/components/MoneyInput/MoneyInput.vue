<template>
  <view class="money-input">
    <!-- 金额输入：带 ¥ 前缀，数字键盘 -->
    <text class="money-input__prefix">¥</text>
    <input
      class="money-input__field"
      type="digit"
      :value="displayValue"
      :placeholder="placeholder || '0.00'"
      @input="onInput"
      @blur="onBlur"
    />
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = withDefaults(
  defineProps<{
    modelValue: number;
    placeholder?: string;
  }>(),
  { placeholder: '0.00' },
);

const emit = defineEmits<{
  (e: 'update:modelValue', value: number): void;
}>();

// 显示值：0 时留空，方便用户直接输入
const displayValue = computed(() => (props.modelValue ? String(props.modelValue) : ''));

// 输入时只保留数字和一个小数点，小数最多 2 位
const onInput = (e: any) => {
  let val = String(e.detail.value || '');
  val = val.replace(/[^\d.]/g, '');
  const parts = val.split('.');
  if (parts.length > 2) val = parts[0] + '.' + parts.slice(1).join('');
  if (parts[1] && parts[1].length > 2) {
    val = parts[0] + '.' + parts[1].slice(0, 2);
  }
  emit('update:modelValue', val === '' ? 0 : parseFloat(val));
};

// 失焦后统一保留两位小数
const onBlur = () => {
  const value = Number(props.modelValue) || 0;
  emit('update:modelValue', Number(value.toFixed(2)));
};
</script>

<style lang="scss" scoped>
.money-input {
  display: flex;
  align-items: center;
  height: 88rpx;
  padding: 0 var(--spacing-md);
  background: var(--color-bg-card);
  border: 1rpx solid var(--color-border);
  border-radius: var(--radius-md);

  &__prefix {
    margin-right: var(--spacing-sm);
    font-size: var(--font-md);
    font-weight: var(--font-weight-medium);
    color: var(--color-text-regular);
  }

  &__field {
    flex: 1;
    height: 100%;
    font-size: var(--font-md);
    font-family: $font-family-number;
  }
}
</style>
