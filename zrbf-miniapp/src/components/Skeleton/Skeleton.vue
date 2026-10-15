<template>
  <view class="skeleton">
    <!-- 按 rows 生成若干灰色占位块，模拟加载中的内容 -->
    <view v-for="row in rows" :key="row" class="skeleton__row" :style="{ width: `${widths[row % widths.length]}%` }" />
  </view>
</template>

<script setup lang="ts">
// 骨架屏：数据没回来之前显示灰色闪烁占位，避免白屏
withDefaults(defineProps<{ rows?: number }>(), { rows: 4 });

const widths = [100, 92, 96, 88];
</script>

<style lang="scss" scoped>
.skeleton {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);

  &__row {
    height: 32rpx;
    background: linear-gradient(90deg, #f0f0f0 25%, #fafafa 50%, #f0f0f0 75%);
    background-size: 200% 100%;
    border-radius: var(--radius-md);
    animation: skeleton-shine 1.2s ease-in-out infinite;
  }
}

@keyframes skeleton-shine {
  0% {
    background-position: 200% 0;
  }

  100% {
    background-position: -200% 0;
  }
}
</style>
