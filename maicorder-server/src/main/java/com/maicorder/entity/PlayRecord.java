package com.maicorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("play_record")
public class PlayRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 关键修改：现在关联的是 GameSession (爸爸)，而不是 CheckIn (爷爷)
    // 对应数据库里的 game_session_id
    private Long gameSessionId;

    // 歌曲名
    private String songName;

    // 难度 (Master, Expert, Another 等)
    private String difficulty;

    // 分数 / 达成率 (101.00%, AAA, 2980 等)
    private String score;

    // 牌子 / 灯 (FC, AP, AJ, HARD 等)
    private String clearStatus;

    // 预留的扩展字段 (如果你以后要存 JSON)
    private String extraDataJson;
}