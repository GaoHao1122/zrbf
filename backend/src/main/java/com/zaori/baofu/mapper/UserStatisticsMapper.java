package com.zaori.baofu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zaori.baofu.entity.UserStatistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收益统计 Mapper
 */
@Mapper
public interface UserStatisticsMapper extends BaseMapper<UserStatistics> {
}
