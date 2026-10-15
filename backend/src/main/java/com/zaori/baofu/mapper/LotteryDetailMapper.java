package com.zaori.baofu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zaori.baofu.entity.LotteryDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 彩票明细 Mapper
 */
@Mapper
public interface LotteryDetailMapper extends BaseMapper<LotteryDetail> {

    /**
     * 按主记录ID查询明细列表
     * 命中索引：idx_record
     */
    @Select("SELECT * FROM lottery_detail WHERE record_id = #{recordId} ORDER BY create_time ASC")
    List<LotteryDetail> listByRecordId(@Param("recordId") Long recordId);

    /**
     * 统计主记录下的明细数量
     */
    @Select("SELECT COUNT(*) FROM lottery_detail WHERE record_id = #{recordId}")
    int countByRecordId(@Param("recordId") Long recordId);
}
