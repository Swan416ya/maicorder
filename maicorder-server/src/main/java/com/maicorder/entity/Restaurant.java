package com.maicorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("restaurant")
public class Restaurant {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;

    // MP 不搞对象关联，直接存 ID 比较省心
    // 之前是 private Arcade arcade; 现在改成：
    private Long arcadeId;

}