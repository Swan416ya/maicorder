package com.maicorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate; // 注意这里改成了 LocalDate

@Data
@TableName("check_in")
public class CheckIn {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long arcadeId;

    // 费用字段
    private Double coinCost;
    private Double foodCost;
    private Double waterCost;
    private Double transportCost;

    private String comment;

    // 修改这里：类型变了
    private LocalDate checkInTime;

    private Long userId;
}