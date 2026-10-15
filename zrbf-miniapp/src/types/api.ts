// 接口类型定义：和后端 DTO 字段一一对应（参考 6.接口设计.md 与后端 dto/response）

// 后端统一返回结构 { code, message, data }
export interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
}

// 盈亏状态：后端返回的字符串
export type ProfitStatus = 'PROFIT' | 'EVEN' | 'LOSS';

// 趋势类型
export type TrendType = 'day' | 'week' | 'month' | 'year';

// ---------- 用户模块 ----------

export interface UserInfo {
  id: number;
  nickname: string;
  avatar: string;
}

export interface LoginResponse {
  token: string;
  user: UserInfo;
}

export interface UserProfileResponse {
  id: number;
  nickname: string;
  avatar: string;
  totalInvestment: number;
  totalProfit: number;
  totalLoss: number;
}

// ---------- 彩票记录模块 ----------

export interface LotteryDetailAddRequest {
  date: string;
  lotteryTypeId: number;
  ticketAmount: number;
  winAmount: number;
  remark?: string;
}

export interface LotteryDetailUpdateRequest {
  lotteryTypeId?: number;
  ticketAmount?: number;
  winAmount?: number;
  remark?: string;
}

// 当日聚合数据（新增明细返回中的 dailyAggregation 字段）
export interface DailyAggregation {
  investAmount: number;
  profitAmount: number;
  netAmount: number;
  profitRate: string;
  status: ProfitStatus;
}

export interface LotteryDetailAddResponse {
  detailId: number;
  recordId: number;
  ticketAmount: number;
  winAmount: number;
  netAmount: number;
  status: ProfitStatus;
  dailyAggregation: DailyAggregation;
}

// 每日详情里的单条明细
export interface DetailItem {
  detailId: number;
  lotteryTypeId: number;
  lotteryTypeName: string;
  ticketAmount: number;
  winAmount: number;
  netAmount: number;
  remark: string;
}

// GET /api/v1/lottery/detail/{date} 的返回
export interface DailyRecordResponse {
  date: string;
  recordId: number;
  investAmount: number;
  profitAmount: number;
  netAmount: number;
  profitRate: string;
  status: ProfitStatus;
  detailCount: number;
  details: DetailItem[];
}

// 历史记录列表中的一项
export interface LotteryListItem {
  recordId: number;
  date: string;
  investAmount: number;
  profitAmount: number;
  netAmount: number;
  profitRate: string;
  status: ProfitStatus;
  detailCount: number;
}

export interface LotteryListResponse {
  total: number;
  list: LotteryListItem[];
}

// ---------- 统计模块 ----------

export interface TodayStatisticsResponse {
  date: string;
  investAmount: number;
  profitAmount: number;
  netAmount: number;
  profitRate: string;
  status: ProfitStatus;
}

// 统计图数据点：周/月/年均返回 date 字符串
export interface StatisticsPoint {
  date: string;
  netAmount: number;
}

export interface WeekStatisticsResponse {
  totalInvestAmount: number;
  totalProfitAmount: number;
  totalNetAmount: number;
  chart: StatisticsPoint[];
}

export interface MonthStatisticsResponse {
  totalInvestAmount: number;
  totalProfitAmount: number;
  totalNetAmount: number;
  chart: StatisticsPoint[];
}

export interface YearStatisticsResponse {
  year: number;
  chart: StatisticsPoint[];
}

// ---------- 首页 Dashboard ----------

export interface DashboardResponse {
  today: {
    investAmount: number;
    netAmount: number;
    profitRate: string;
    status: ProfitStatus;
  };
  summary: {
    totalInvestAmount: number;
    totalProfitAmount: number;
    totalNetAmount: number;
    winRate: string;
  };
  chart: {
    type: TrendType;
    data: TrendPoint[];
  };
}

// ---------- 图表 ----------

// 趋势图数据点（x 是日期/标签，y 是盈亏金额）
export interface TrendPoint {
  x: string;
  y: number;
}
