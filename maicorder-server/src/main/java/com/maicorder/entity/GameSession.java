package com.maicorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.List;

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

    // 记得生成 Getter/Setter ！(Alt + Insert)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCheckInId() { return checkInId; }
    public void setCheckInId(Long checkInId) { this.checkInId = checkInId; }
    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }
    public Integer getPcCount() { return pcCount; }
    public void setPcCount(Integer pcCount) { this.pcCount = pcCount; }
    public String getCurrentRating() { return currentRating; }
    public void setCurrentRating(String currentRating) { this.currentRating = currentRating; }
    public List<PlayRecord> getRecords() { return records; }
    public void setRecords(List<PlayRecord> records) { this.records = records; }
}