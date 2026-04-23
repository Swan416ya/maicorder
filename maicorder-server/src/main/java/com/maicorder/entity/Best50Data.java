package com.maicorder.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class Best50Data {
    // JSON 中是数字类型，建议用 Integer 而非 String
    private Integer rating;

    // 映射 JSON 中的 rating_b35
    @JsonProperty("rating_b35")
    private Integer ratingB35;

    // 映射 JSON 中的 rating_b15
    @JsonProperty("rating_b15")
    private Integer ratingB15;

    // 映射 JSON 中的 scores_b35
    @JsonProperty("scores_b35")
    private List<Score> scoresB35;

    // 映射 JSON 中的 scores_b15
    @JsonProperty("scores_b15")
    private List<Score> scoresB15;

    @Data
    public static class Score {
        private Integer id;
        private String level;

        // 映射 JSON 中的 level_index
        @JsonProperty("level_index")
        private Integer levelIndex;

        private Double achievements;
        private Object fc;
        private Object fs;

        // 映射 JSON 中的 dx_score
        @JsonProperty("dx_score")
        private Integer dxScore;

        // 映射 JSON 中的 dx_rating
        @JsonProperty("dx_rating")
        private Double dxRating;

        // 映射 JSON 中的 play_count
        @JsonProperty("play_count")
        private Object playCount;

        // 映射 JSON 中的 play_time
        @JsonProperty("play_time")
        private Object playTime;

        private Integer rate;
        private String type;
        private String title;

        // 映射 JSON 中的 level_value
        @JsonProperty("level_value")
        private Double levelValue;

        // 映射 JSON 中的 level_dx_score
        @JsonProperty("level_dx_score")
        private Integer levelDxScore;

        // 映射 JSON 中的 dx_star
        @JsonProperty("dx_star")
        private Integer dxStar;

        private Integer version;
    }
}