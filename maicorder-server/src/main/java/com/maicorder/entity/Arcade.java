package com.maicorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("arcade") // 指定数据库表名
public class Arcade {

    @TableId(type = IdType.AUTO) // 指定主键自增
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String province;
    private String city;
    private String district;
}