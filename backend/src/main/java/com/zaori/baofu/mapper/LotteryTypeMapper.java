package com.zaori.baofu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zaori.baofu.entity.LotteryType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 彩票类型字典 Mapper
 */
@Mapper
public interface LotteryTypeMapper extends BaseMapper<LotteryType> {
}
