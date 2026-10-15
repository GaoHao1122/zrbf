<template>
  <view class="profit-ring" :style="{ width: `${size}rpx`, height: `${size}rpx` }">
    <canvas :id="canvasId" type="2d" class="profit-ring__canvas"></canvas>
    <view class="profit-ring__center">
      <text class="profit-ring__value">{{ displayValue }}</text>
      <text v-if="label" class="profit-ring__label">{{ label }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, nextTick, onMounted, ref, watch } from 'vue';

// 收益率环形图：percent 0~100，用 canvas 画一个进度圆环
const props = withDefaults(
  defineProps<{
    percent: number;
    size?: number;
    color?: string;
    trackColor?: string;
    label?: string;
  }>(),
  { size: 160, color: '#FF6B35', trackColor: '#F0F0F0', label: '' },
);

const canvasId = `profit-ring-${Math.random().toString(36).slice(2, 10)}`;
const instance = getCurrentInstance();

const displayValue = computed(() => {
  const v = Math.max(0, Math.min(100, Number(props.percent) || 0));
  return `${v.toFixed(0)}%`;
});

// 在小程序里 canvas 需要用选择器拿到节点后绘制
const draw = async () => {
  await nextTick();

  // #ifdef MP-WEIXIN
  const query = uni.createSelectorQuery().in(instance);
  (query
    .select(`#${canvasId}`) as any)
    .fields({ node: true, size: true })
    .exec((res: any[]) => {
      const info = res && res[0];
      const canvas = info && info.node;
      if (!canvas) return;
      const dpr = uni.getSystemInfoSync().pixelRatio || 2;
      canvas.width = info.width * dpr;
      canvas.height = info.height * dpr;
      const ctx = canvas.getContext('2d');
      ctx.scale(dpr, dpr);
      paint(ctx, info.width, info.height);
    });
  // #endif

  // #ifdef H5
  const canvas = document.getElementById(canvasId) as HTMLCanvasElement | null;
  if (canvas) {
    const ctx = canvas.getContext('2d');
    if (ctx) paint(ctx, canvas.clientWidth || props.size, canvas.clientHeight || props.size);
  }
  // #endif
};

// 真正的画图逻辑
const paint = (ctx: CanvasRenderingContext2D, width: number, height: number) => {
  const ratio = Math.max(0, Math.min(100, Number(props.percent) || 0)) / 100;
  const lineWidth = Math.max(6, width * 0.12);
  const centerX = width / 2;
  const centerY = height / 2;
  const radius = Math.min(width, height) / 2 - lineWidth;

  ctx.clearRect(0, 0, width, height);

  // 底环
  ctx.beginPath();
  ctx.arc(centerX, centerY, radius, 0, Math.PI * 2);
  ctx.strokeStyle = props.trackColor;
  ctx.lineWidth = lineWidth;
  ctx.stroke();

  // 进度环（从 12 点方向顺时针）
  if (ratio > 0) {
    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, -Math.PI / 2, -Math.PI / 2 + Math.PI * 2 * ratio);
    ctx.strokeStyle = props.color;
    ctx.lineWidth = lineWidth;
    ctx.lineCap = 'round';
    ctx.stroke();
  }
};

watch(() => props.percent, draw);
onMounted(draw);
</script>

<style lang="scss" scoped>
.profit-ring {
  position: relative;

  &__canvas {
    width: 100%;
    height: 100%;
  }

  &__center {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  &__value {
    font-size: var(--font-lg);
    font-weight: var(--font-weight-semibold);
    font-family: $font-family-number;
    color: var(--color-text-primary);
  }

  &__label {
    margin-top: 4rpx;
    font-size: var(--font-xs);
    color: var(--color-text-secondary);
  }
}
</style>
