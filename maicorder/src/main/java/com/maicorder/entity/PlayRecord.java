package com.maicorder.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
public class PlayRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 必须知道这条战绩属于哪次出勤
    @ManyToOne
    @JoinColumn(name = "check_in_id")
    private CheckIn checkIn;

    // 游戏类型 (Maimai? SDVX?)
    @Enumerated(EnumType.STRING)
    private GameType game;

    // --- 通用成绩字段 ---

    // Rating / Volforce / 段位
    // 存 String 是为了兼容 "16000" (Maimai) 和 "八段" (IIDX)
    private String ratingResult;

    // 具体的 Rating 变化值，比如 +15
    private Integer ratingChange;

    // --- 扩展字段 (为了你的进阶需求) ---

    // 歌曲名 (未来这里可以换成 song_id 关联 Song 表)
    private String songName;

    // 难度 (Master, Another, Maximum)
    private String difficulty;

    // 达成率 / 分数 (100.5000%, AAA)
    private String score;

    // 牌子/灯 (AP, FC, HARD CLEAR)
    private String clearStatus;

    // 这是一个特殊的技巧：如果以后你要存特别复杂的东西，
    // 比如 SDVX 的很多小项数据，可以预留一个大的文本字段存 JSON
    // 目前新手阶段先不折腾 MySQL JSON 类型，用 String 顶一下
    @Column(columnDefinition = "TEXT")
    private String extraDataJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CheckIn getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(CheckIn checkIn) {
        this.checkIn = checkIn;
    }

    public GameType getGame() {
        return game;
    }

    public void setGame(GameType game) {
        this.game = game;
    }

    public String getRatingResult() {
        return ratingResult;
    }

    public void setRatingResult(String ratingResult) {
        this.ratingResult = ratingResult;
    }

    public Integer getRatingChange() {
        return ratingChange;
    }

    public void setRatingChange(Integer ratingChange) {
        this.ratingChange = ratingChange;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getClearStatus() {
        return clearStatus;
    }

    public void setClearStatus(String clearStatus) {
        this.clearStatus = clearStatus;
    }

    public String getExtraDataJson() {
        return extraDataJson;
    }

    public void setExtraDataJson(String extraDataJson) {
        this.extraDataJson = extraDataJson;
    }
}