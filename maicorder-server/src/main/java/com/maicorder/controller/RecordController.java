package com.maicorder.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maicorder.common.Result;
import com.maicorder.entity.CheckIn;
import com.maicorder.entity.GameSession;
import com.maicorder.entity.PlayRecord;
import com.maicorder.mapper.CheckInMapper;
import com.maicorder.mapper.GameSessionMapper;
import com.maicorder.mapper.PlayRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记录控制器
 * 负责处理签到记录、游戏场次和战绩的查询操作
 */
@RestController
@RequestMapping("/api/records")
public class RecordController {

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private GameSessionMapper gameSessionMapper;

    @Autowired
    private PlayRecordMapper playRecordMapper;

    /**
     * 获取用户的签到记录列表
     * @param userId 用户ID
     * @return 签到记录列表
     */
    @GetMapping("/checkins/{userId}")
    public Result<List<CheckIn>> getUserCheckIns(@PathVariable Long userId) {
        try {
            QueryWrapper<CheckIn> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.orderByDesc("check_in_time"); // 按签到时间倒序
            List<CheckIn> checkIns = checkInMapper.selectList(queryWrapper);
            return Result.success(checkIns);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(500, "获取签到记录失败：" + e.getMessage());
        }
    }


    /**
     * 获取签到记录详情（包含游戏场次和战绩）
     * @param checkInId 签到记录ID
     * @return 签到记录详情
     */
    @GetMapping("/checkins/detail/{checkInId}")
    public Result<Map<String, Object>> getCheckInDetail(@PathVariable Long checkInId) {
        try {
            // 获取签到记录
            CheckIn checkIn = checkInMapper.selectById(checkInId);
            if (checkIn == null) {
                return Result.fail(404, "签到记录不存在");
            }

            // 获取关联的游戏场次
            QueryWrapper<GameSession> sessionQuery = new QueryWrapper<>();
            sessionQuery.eq("check_in_id", checkInId);
            List<GameSession> gameSessions = gameSessionMapper.selectList(sessionQuery);

            // 为每个游戏场次获取关联的战绩
            for (GameSession session : gameSessions) {
                QueryWrapper<PlayRecord> recordQuery = new QueryWrapper<>();
                recordQuery.eq("game_session_id", session.getId());
                List<PlayRecord> records = playRecordMapper.selectList(recordQuery);
                session.setRecords(records);
            }

            // 构建响应数据
            Map<String, Object> detail = new HashMap<>();
            detail.put("checkIn", checkIn);
            detail.put("gameSessions", gameSessions);

            return Result.success(detail);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(500, "获取签到详情失败：" + e.getMessage());
        }
    }

    /**
     * 按日期范围获取用户的签到记录
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 签到记录列表
     */
    @GetMapping("/checkins/date/{userId}")
    public Result<List<CheckIn>> getCheckInsByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            QueryWrapper<CheckIn> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.between("check_in_time", startDate, endDate);
            queryWrapper.orderByDesc("check_in_time"); // 按签到时间倒序
            List<CheckIn> checkIns = checkInMapper.selectList(queryWrapper);
            return Result.success(checkIns);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(500, "获取签到记录失败：" + e.getMessage());
        }
    }

    /**
     * 分页获取用户的签到记录
     * @param userId 用户ID
     * @param page 当前页码
     * @param size 每页大小
     * @return 分页签到记录
     */
    @GetMapping("/checkins/page/{userId}")
    public Result<Map<String, Object>> getCheckInsByPage(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            // 构建分页对象
            Page<CheckIn> checkInPage = new Page<>(page, size);

            // 构建查询条件
            QueryWrapper<CheckIn> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.orderByDesc("check_in_time"); // 按签到时间倒序

            // 执行分页查询
            IPage<CheckIn> resultPage = checkInMapper.selectPage(checkInPage, queryWrapper);

            // 构建响应数据
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("records", resultPage.getRecords());
            pageData.put("total", resultPage.getTotal());
            pageData.put("current", resultPage.getCurrent());
            pageData.put("size", resultPage.getSize());
            pageData.put("pages", resultPage.getPages());

            return Result.success(pageData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(500, "分页获取签到记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的游戏战绩统计
     * @param userId 用户ID
     * @return 战绩统计信息
     */
    @GetMapping("/stats/{userId}")
    public Result<Map<String, Object>> getUserStats(@PathVariable Long userId) {
        try {
            // 获取用户的签到次数
            QueryWrapper<CheckIn> checkInQuery = new QueryWrapper<>();
            checkInQuery.eq("user_id", userId);
            Long checkInCount = checkInMapper.selectCount(checkInQuery);

            // 获取用户的游戏场次数量
            QueryWrapper<GameSession> sessionQuery = new QueryWrapper<>();
            sessionQuery.eq("user_id", userId);
            Long sessionCount = gameSessionMapper.selectCount(sessionQuery);

            // 获取用户的战绩数量
            QueryWrapper<PlayRecord> recordQuery = new QueryWrapper<>();
            recordQuery.eq("user_id", userId);
            Long recordCount = playRecordMapper.selectCount(recordQuery);

            // 构建响应数据
            Map<String, Object> stats = new HashMap<>();
            stats.put("checkInCount", checkInCount);
            stats.put("sessionCount", sessionCount);
            stats.put("recordCount", recordCount);

            return Result.success(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(500, "获取统计信息失败：" + e.getMessage());
        }
    }
}