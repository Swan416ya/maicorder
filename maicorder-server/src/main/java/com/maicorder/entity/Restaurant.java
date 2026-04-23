package com.maicorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("restaurant")
public class Restaurant {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;

    // MP 不搞对象关联，直接存 ID 比较省心
    // 之前是 private Arcade arcade; 现在改成：
    private Long arcadeId;

    // Getter & Setter...

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

    public Long getArcadeId() {
        return arcadeId;
    }

    public void setArcadeId(Long arcadeId) {
        this.arcadeId = arcadeId;
    }
}