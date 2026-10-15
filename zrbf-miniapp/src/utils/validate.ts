// 表单校验：和后端规则保持一致（前端校验只是体验优化，后端仍会校验）

// 金额校验：必填、非负、最大 7 位整数 + 2 位小数
export const validateAmount = (value: number): string | null => {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return '请输入正确金额';
  }
  if (value < 0) {
    return '金额不能为负数';
  }
  if (value > 9999999.99) {
    return '金额超出合理范围';
  }
  return null;
};

// 日期校验：必填，且不能晚于今天
export const validateDate = (date: string): string | null => {
  if (!date) return '请选择日期';
  const today = new Date();
  today.setHours(23, 59, 59, 999);
  if (new Date(date).getTime() > today.getTime()) {
    return '日期不可超过当前日期';
  }
  return null;
};

// 备注校验：选填，最多 100 个字符
export const validateRemark = (remark: string): string | null => {
  if (remark && remark.length > 100) {
    return '备注最多 100 字符';
  }
  return null;
};
