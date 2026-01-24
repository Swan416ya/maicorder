package com.maicorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("arcade") // 指定数据库表名
public class Arcade {

    @TableId(type = IdType.AUTO) // 指定主键自增
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;

    // ... 下面是 Getter 和 Setter (你自己生成的那些，不用动) ...
    // 为了节省篇幅，我这里不写 Getter/Setter 了，你保留你原本生成的代码即可

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    // 记得删掉原本所有的 import jakarta.persistence.*;
}