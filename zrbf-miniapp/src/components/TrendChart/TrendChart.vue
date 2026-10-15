<template>
  <view class="trend-chart">
    <!-- 统一用 canvas：微信小程序用 type=2d 画布节点，H5 直接用 DOM canvas -->
    <canvas :id="canvasId" type="2d" class="trend-chart__canvas"></canvas>
    <view v-if="!data.length" class="trend-chart__empty">
      <text class="trend-chart__empty-text">暂无趋势数据</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, nextTick, onMounted, onUnmounted, ref, watch } from 'vue';
import * as echarts from 'echarts/core';
import { LineChart } from 'echarts/charts';
import { GridComponent, TooltipComponent } from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';
import { formatSignedMoney } from '@/utils/format';
import type { TrendPoint, TrendType } from '@/types/api';

// 按需注册 ECharts 模块，减小打包体积
echarts.use([LineChart, GridComponent, TooltipComponent, CanvasRenderer]);

const props = withDefaults(
  defineProps<{
    // 图表数据：{ x: 日期/标签, y: 盈亏金额 }
    data: TrendPoint[];
    type?: TrendType;
  }>(),
  { type: 'week' },
);

const canvasId = `trend-chart-${Math.random().toString(36).slice(2, 10)}`;
const chart = ref<any>(null);
const instance = getCurrentInstance();

// 构建 ECharts 配置（颜色遵循设计 token）
const buildOption = () => {
  const xData = props.data.map((p) => p.x);
  const yData = props.data.map((p) => p.y);
  const showSymbol = props.data.length <= 30;

  return {
    grid: { left: 8, right: 16, top: 24, bottom: 8, containLabel: true },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const p = params[0];
        return `${p.axisValue}<br/>盈亏：${formatSignedMoney(p.value)}`;
      },
    },
    xAxis: {
      type: 'category',
      data: xData,
      axisLine: { lineStyle: { color: '#E5E5E5' } },
      axisLabel: { color: '#8C8C8C', fontSize: 10 },
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: '#8C8C8C', fontSize: 10 },
      splitLine: { lineStyle: { color: '#F5F5F5' } },
    },
    series: [
      {
        type: 'line',
        data: yData,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        showSymbol,
        lineStyle: { color: '#FF6B35', width: 2 },
        itemStyle: { color: '#FF6B35' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255,107,53,0.2)' },
            { offset: 1, color: 'rgba(255,107,53,0)' },
          ]),
        },
        markLine: {
          silent: true,
          symbol: 'none',
          data: [{ yAxis: 0 }],
          lineStyle: { color: '#BFBFBF', type: 'dashed' },
        },
      },
    ],
  };
};

// 初始化图表：H5 用 DOM，微信小程序用 canvas 节点
const initChart = async () => {
  await nextTick();
  if (!props.data.length) return;

  // #ifdef H5
  const el = document.getElementById(canvasId) as HTMLCanvasElement | null;
  if (el) {
    chart.value = echarts.init(el);
    chart.value.setOption(buildOption());
  }
  // #endif

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
      chart.value = echarts.init(canvas, null, {
        width: info.width,
        height: info.height,
        devicePixelRatio: dpr,
      });
      chart.value.setOption(buildOption());
    });
  // #endif
};

// 数据变化时刷新图表
watch(
  () => props.data,
  async () => {
    await nextTick();
    // 数据清空时清理旧图，避免残留上一次的折线
    if (!props.data.length) {
      if (chart.value) chart.value.clear();
      return;
    }
    if (chart.value) {
      chart.value.setOption(buildOption(), true);
    } else {
      await initChart();
    }
  },
  { deep: true },
);

onMounted(initChart);

// 页面卸载时销毁实例，避免内存泄漏
onUnmounted(() => {
  if (chart.value) {
    chart.value.dispose();
    chart.value = null;
  }
});
</script>

<style lang="scss" scoped>
.trend-chart {
  position: relative;
  width: 100%;
  height: 400rpx;

  &__canvas {
    width: 100%;
    height: 100%;
  }

  &__empty {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__empty-text {
    font-size: var(--font-sm);
    color: var(--color-text-secondary);
  }
}
</style>
