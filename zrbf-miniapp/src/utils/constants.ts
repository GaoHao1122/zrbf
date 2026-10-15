// 全局常量：盈亏状态、彩票类型、趋势类型

// 盈亏状态：与后端接口返回的字符串一致
export const PROFIT_STATUS = {
  PROFIT: 'PROFIT', // 盈利
  EVEN: 'EVEN', // 持平
  LOSS: 'LOSS', // 亏损
} as const;

export type ProfitStatusKey = keyof typeof PROFIT_STATUS;

// 状态展示文案与颜色（中国习惯红涨绿跌）
export const PROFIT_STATUS_META: Record<string, { label: string; color: string; bg: string }> = {
  PROFIT: { label: '盈利', color: 'var(--color-profit)', bg: '#fff1f0' },
  EVEN: { label: '持平', color: 'var(--color-even)', bg: '#f5f5f5' },
  LOSS: { label: '亏损', color: 'var(--color-loss)', bg: '#f6ffed' },
};

// 彩票类型（对应 schema.sql 的 lottery_type 初始数据）
export const LOTTERY_TYPE_OPTIONS = [
  { id: 1, name: '大乐透', code: 'DLT' },
  { id: 2, name: '排列三', code: 'PL3' },
  { id: 3, name: '排列五', code: 'PL5' },
  { id: 4, name: '七星彩', code: 'QXC' },
] as const;

// 趋势页 Tab
export const TREND_TABS = [
  { type: 'week', label: '周' },
  { type: 'month', label: '月' },
  { type: 'year', label: '年' },
] as const;

export type TrendTabType = (typeof TREND_TABS)[number]['type'];
