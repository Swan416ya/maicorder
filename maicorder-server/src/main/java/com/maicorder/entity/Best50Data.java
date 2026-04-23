package com.maicorder.entity;

import lombok.Data;
import java.util.List;

@Data
public class Best50Data {
    private Integer rating;
    private Integer ratingB35;
    private Integer ratingB15;
    private List<Score> scoresB35;
    private List<Score> scoresB15;

    @Data
    public static class Score {
        private Integer id;
        private String level;
        private Integer levelIndex;
        private Double achievements;
        private Object fc;
        private Object fs;
        private Integer dxScore;
        private Double dxRating;
        private Object playCount;
        private Object playTime;
        private Integer rate;
        private String type;
        private String title;
        private Double levelValue;
        private Integer levelDxScore;
        private Integer dxStar;
        private Integer version;
    }
}