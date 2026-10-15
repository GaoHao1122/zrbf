<template>
  <!-- 日期选择器：封装 uni-app 原生 picker，统一样式 -->
  <picker
    mode="date"
    :value="modelValue"
    :start="start"
    :end="end"
    @change="onChange"
  >
    <view class="date-picker">
      <text :class="['date-picker__text', { 'is-placeholder': !modelValue }]">
        {{ modelValue || placeholder }}
      </text>
      <text class="date-picker__arrow">›</text>
    </view>
  </picker>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    modelValue?: string;
    placeholder?: string;
    start?: string;
    end?: string;
  }>(),
  { modelValue: '', placeholder: '请选择日期', start: '', end: '' },
);

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
}>();

const onChange = (e: any) => {
  emit('update:modelValue', e.detail.value);
};
</script>

<style lang="scss" scoped>
.date-picker {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  min-height: 56rpx;

  &__text {
    font-size: var(--font-base);
    color: var(--color-text-primary);

    &.is-placeholder {
      color: var(--color-text-placeholder);
    }
  }

  &__arrow {
    font-size: var(--font-lg);
    color: var(--color-text-placeholder);
  }
}
</style>
