// 平台判断工具：区分微信小程序 / H5

// H5 环境存在 window 和 document
export const isH5 = typeof window !== 'undefined' && typeof document !== 'undefined';

// 非 H5 即视为小程序环境（当前项目只面向微信小程序与 H5）
export const isMpWeixin = !isH5;
