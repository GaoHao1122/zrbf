// 金额、日期、百分比格式化工具

// 金额格式化：1234.5 -> "1234.50"
export const formatMoney = (amount: number | string | null | undefined, decimals = 2): string => {
  const n = Number(amount);
  if (amount === null || amount === undefined || Number.isNaN(n)) {
    return '0.00';
  }
  return n.toFixed(decimals);
};

// 带符号金额：盈利前面加 +，亏损显示负数
export const formatSignedMoney = (amount: number | string | null | undefined): string => {
  const n = Number(amount ?? 0);
  if (Number.isNaN(n)) return '0.00';
  const fixed = n.toFixed(2);
  return n > 0 ? `+${fixed}` : fixed;
};

// 日期格式化：Date 或字符串转成 YYYY-MM-DD
export const formatDate = (date: Date | string | number): string => {
  if (typeof date === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(date)) {
    return date;
  }
  const d = new Date(date);
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
};

// 今天日期字符串
export const todayStr = (): string => formatDate(new Date());

// 百分比格式化：后端可能返回 "66.67%" 或数字，统一补上 %
export const formatPercent = (rate: string | number | null | undefined): string => {
  if (rate === null || rate === undefined || rate === '') return '0%';
  const s = String(rate);
  if (s.includes('%')) return s;
  const n = Number(rate);
  return Number.isNaN(n) ? '0%' : `${n.toFixed(2)}%`;
};
