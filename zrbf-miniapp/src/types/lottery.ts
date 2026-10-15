// 彩票业务相关的前端类型

// 彩票类型（对应后端 lottery_type 字典表初始数据）
export interface LotteryTypeOption {
  id: number;
  name: string;
  code: string;
}

// 今日登记页的表单数据
export interface LotteryForm {
  date: string;
  lotteryTypeId: number;
  ticketAmount: number;
  winAmount: number;
  remark: string;
}

// 编辑明细页需要携带的参数
export interface EditDetailParams {
  detailId: string;
  date: string;
}
