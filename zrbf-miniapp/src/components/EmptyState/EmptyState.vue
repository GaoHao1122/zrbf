<template>
  <view class="empty-state">
    <!-- 可选图片，不传就显示一个简单的圆形占位 -->
    <image v-if="image" class="empty-state__image" :src="image" mode="aspectFit" />
    <view v-else class="empty-state__placeholder">
      <text class="empty-state__placeholder-text">空</text>
    </view>

    <text class="empty-state__title">{{ title }}</text>
    <text v-if="description" class="empty-state__desc">{{ description }}</text>

    <button v-if="actionText" class="empty-state__action" @tap="$emit('action')">
      {{ actionText }}
    </button>
  </view>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    image?: string;
    title: string;
    description?: string;
    actionText?: string;
  }>(),
  { image: '', description: '', actionText: '' },
);

defineEmits<{
  (e: 'action'): void;
}>();
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl) var(--spacing-lg);

  &__image {
    width: 200rpx;
    height: 200rpx;
  }

  &__placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 160rpx;
    height: 160rpx;
    background: var(--color-primary-light);
    border-radius: var(--radius-full);
  }

  &__placeholder-text {
    font-size: var(--font-xl);
    color: var(--color-primary);
  }

  &__title {
    margin-top: var(--spacing-lg);
    font-size: var(--font-base);
    font-weight: var(--font-weight-medium);
    color: var(--color-text-primary);
  }

  &__desc {
    margin-top: var(--spacing-xs);
    font-size: var(--font-sm);
    color: var(--color-text-secondary);
  }

  &__action {
    margin-top: var(--spacing-lg);
    padding: 16rpx 48rpx;
    font-size: var(--font-base);
    color: #ffffff;
    background: var(--color-primary);
    border-radius: var(--radius-full);
  }
}
</style>
