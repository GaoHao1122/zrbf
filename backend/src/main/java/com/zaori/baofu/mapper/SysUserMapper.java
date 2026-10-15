package com.zaori.baofu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zaori.baofu.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户表 Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据 openid 查询用户
     */
    @Select("SELECT * FROM sys_user WHERE openid = #{openid} LIMIT 1")
    SysUser selectByOpenid(@Param("openid") String openid);
}
