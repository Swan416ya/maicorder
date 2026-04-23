package com.maicorder.controller;

import com.maicorder.common.Result;
import com.maicorder.entity.Arcade;
import com.maicorder.mapper.ArcadeMapper;
import com.maicorder.service.ArcadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/arcades") // 保持原有路径（注意前端要对应：/api/arcades/names）
public class ArcadeController {

    // 1. 替换注入：从 ArcadeMapper 改为 ArcadeService（业务逻辑交给Service层）
    @Autowired
    private ArcadeService arcadeService;
    @Autowired private ArcadeMapper arcadeMapper;

    // ========== 原有接口（保留，不影响旧功能） ==========
    @GetMapping
    public Result<List<Arcade>> list() {
        List<Arcade> arcadeList = arcadeService.list(); // 改用Service的list方法（MyBatis-Plus内置）
        return Result.success(arcadeList);
    }

    @PostMapping
    public Result<Arcade> create(@RequestBody Arcade arcade) {
        arcadeService.save(arcade); // 改用Service的save方法（MyBatis-Plus内置）
        return Result.success(arcade);
    }

    // ========== 新增：批量查询机厅名称接口 ==========
    /**
     * 批量根据机厅ID查询名称映射
     * 请求方式：POST
     * 请求路径：/api/arcades/names
     * 请求体：[1,2,3]（机厅ID列表）
     * 返回值：{1:"机厅A", 2:"机厅B"}（ID→名称的Map）
     */
    @PostMapping("/names")
    public Result<Map<Long, String>> getArcadeNames(@RequestBody List<Long> arcadeIds) {
        // 调用Service层的方法，获取ID→名称的Map
        Map<Long, String> nameMap = arcadeService.getArcadeNameMapByIds(arcadeIds);
        // 返回结果（用Result.success适配你的统一返回格式）
        return Result.success(nameMap);
    }

    @PostMapping("/add-arcade")
    public Result<Arcade> addArcade(@RequestBody Arcade arcade) {
        //
        System.out.println(arcade);
        try {
            arcadeMapper.insert(arcade);
        } catch (Exception e) {
            return Result.fail(500, "添加机厅失败");
        }
        return Result.success(arcade);
    }
    
}

