package com.maicorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maicorder.entity.Restaurant;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 一定要加这个注解
public interface RestaurantMapper extends BaseMapper<Restaurant> {
}