package com.maicorder.service;

import com.maicorder.entity.Arcade;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

/**
 * 机厅业务层接口
 * 继承 MyBatis-Plus 的 IService，获得基础 CRUD 方法（无需手写增删改查）
 */
public interface ArcadeService extends IService<Arcade> {

    /**
     * 批量根据机厅ID查询名称映射
     * @param ids 机厅ID列表
     * @return key=机厅ID，value=机厅名称的Map（查不到的ID不会出现在Map中）
     */
    Map<Long, String> getArcadeNameMapByIds(List<Long> ids);
}