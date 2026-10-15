package com.zaori.baofu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zaori.baofu.entity.LotteryRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * 彩票投资主记录 Mapper
 */
@Mapper
public interface LotteryRecordMapper extends BaseMapper<LotteryRecord> {

    /**
     * 根据用户ID和日期查询主记录
     * 命中索引：uk_user_date
     */
    @Select("SELECT * FROM lottery_record WHERE user_id = #{userId} AND record_date = #{date} LIMIT 1")
    LotteryRecord selectByUserDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
