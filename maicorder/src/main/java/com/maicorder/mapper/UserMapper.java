package com.maicorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maicorder.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}