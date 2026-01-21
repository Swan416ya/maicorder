package com.maicorder.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description; // 比如 "这家店牛肉饭好吃"

    // 关键点：关联到机厅
    @ManyToOne
    @JoinColumn(name = "arcade_id")
    private Arcade arcade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Arcade getArcade() {
        return arcade;
    }

    public void setArcade(Arcade arcade) {
        this.arcade = arcade;
    }
}