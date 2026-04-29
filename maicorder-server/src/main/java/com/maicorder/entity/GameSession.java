package com.maicorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.List;

import lombok.Data;

@Data
@TableName("game_session")
public class GameSession {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long checkInId;    // 关联爷爷
    private String gameName;
    private Integer pcCount;
    private String currentRating;

    // 辅助字段：用来接收前端传来的战绩列表
    @TableField(exist = false)
    private List<PlayRecord> records;

}