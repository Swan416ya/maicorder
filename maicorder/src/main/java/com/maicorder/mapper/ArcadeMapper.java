package com.maicorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maicorder.entity.Arcade;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 一定要加这个注解
public interface ArcadeMapper extends BaseMapper<Arcade> {
}