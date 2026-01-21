package com.maicorder.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联机厅
    @ManyToOne
    @JoinColumn(name = "arcade_id")
    private Arcade arcade;

    // 关联餐厅 (可选，这次出勤去吃了哪家)
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private Double totalMoneySpent; // 投币金额
    private Double transportCost;   // 交通费
    private Double foodCost;        // 饭钱

    private LocalDateTime checkInTime; // 出勤时间
    private String comment; // 总备注： "今天手感不错"

    // 重点：一对多关联
    // mappedBy 表示由 PlayRecord 那边的 "checkIn" 字段来维护关系
    // CascadeType.ALL 表示如果你删了这个出勤记录，里面的战绩也会一起被删掉 (类似 Django on_delete=CASCADE)
    @OneToMany(mappedBy = "checkIn", cascade = CascadeType.ALL)
    private List<PlayRecord> playRecords;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Arcade getArcade() {
        return arcade;
    }

    public void setArcade(Arcade arcade) {
        this.arcade = arcade;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Double getTotalMoneySpent() {
        return totalMoneySpent;
    }

    public void setTotalMoneySpent(Double totalMoneySpent) {
        this.totalMoneySpent = totalMoneySpent;
    }

    public Double getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(Double transportCost) {
        this.transportCost = transportCost;
    }

    public Double getFoodCost() {
        return foodCost;
    }

    public void setFoodCost(Double foodCost) {
        this.foodCost = foodCost;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<PlayRecord> getPlayRecords() {
        return playRecords;
    }

    public void setPlayRecords(List<PlayRecord> playRecords) {
        this.playRecords = playRecords;
    }
}