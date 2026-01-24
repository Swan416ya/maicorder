package com.maicorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maicorder.entity.GameSession;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameSessionMapper extends BaseMapper<GameSession> {
    // 里面什么都不用写，空的就行
}