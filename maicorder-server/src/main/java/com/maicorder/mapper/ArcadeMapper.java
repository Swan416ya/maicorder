package com.maicorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maicorder.entity.Arcade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 机厅数据访问层
 * 继承 MyBatis-Plus 的 BaseMapper，获得基础 CRUD 方法
 */
@Mapper
public interface ArcadeMapper extends BaseMapper<Arcade> {

    /**
     * 批量根据机厅ID查询机厅ID和名称的映射
     * 修复点：使用 MyBatis 的 foreach 标签遍历 ID 列表，解决批量 IN 查询失效问题
     * @param ids 机厅ID列表
     * @return 包含id和name的Arcade对象列表（仅返回id和name字段，减少数据传输）
     */
    @Select("<script>" +
            "SELECT id, name FROM arcade " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Arcade> listNamesByIds(@Param("ids") List<Long> ids);
}