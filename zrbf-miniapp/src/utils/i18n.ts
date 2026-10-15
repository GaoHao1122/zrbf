// 国际化预留工具
// 目前只是简单的中文字典查询，后续可替换为 vue-i18n 的 $t

import zhCN from '@/locales/zh-CN';

// 按 key 点路径查找文案，例如 t('dashboard.title')
export const t = (key: string): string => {
  const value = key
    .split('.')
    .reduce<unknown>((obj, part) => (obj == null ? obj : (obj as Record<string, unknown>)[part]), zhCN);
  return typeof value === 'string' ? value : key;
};
