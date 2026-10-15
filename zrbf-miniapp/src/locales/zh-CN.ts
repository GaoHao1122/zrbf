// 中文文案字典（国际化预留）
// V1.0 暂未接入 vue-i18n，后续需要多语言时通过 t() 或 $t('key') 使用
export default {
  common: {
    confirm: '确定',
    cancel: '取消',
    loading: '加载中',
    noMore: '没有更多了',
    retry: '重新加载',
  },
  dashboard: {
    title: '早日暴富',
    todayProfit: '今日盈亏',
    todayInvest: '今日投入',
    todayRate: '今日收益率',
    totalStats: '累计统计',
    weekTrend: '本周趋势',
    goAdd: '今日登记',
    history: '历史记录',
    trend: '趋势分析',
    todayDetail: '当日详情',
  },
  lottery: {
    addTitle: '今日登记',
    listTitle: '历史记录',
    detailTitle: '当日详情',
    editTitle: '编辑明细',
    date: '日期',
    type: '彩票类型',
    ticketAmount: '投入本金（元）',
    winAmount: '中奖金额（元）',
    remark: '备注',
    save: '保存记录',
    saveSuccess: '保存成功',
  },
} as const;
