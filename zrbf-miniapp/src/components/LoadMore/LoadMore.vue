<template>
  <view class="load-more">
    <!-- 列表触底加载的三种状态 -->
    <text v-if="empty" class="load-more__text">暂无数据</text>
    <text v-else-if="loading" class="load-more__text">加载中...</text>
    <text v-else-if="finished" class="load-more__text">— 没有更多了 —</text>
  </view>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    loading?: boolean;
    finished?: boolean;
    empty?: boolean;
  }>(),
  { loading: false, finished: false, empty: false },
);
</script>

<style lang="scss" scoped>
.load-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-lg);

  &__text {
    font-size: var(--font-sm);
    color: var(--color-text-secondary);
  }
}
</style>
