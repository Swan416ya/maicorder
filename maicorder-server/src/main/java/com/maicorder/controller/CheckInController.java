package com.maicorder.controller;

import com.maicorder.common.Result;
import com.maicorder.entity.CheckIn;
import com.maicorder.entity.GameSession;
import com.maicorder.entity.PlayRecord;
import com.maicorder.mapper.CheckInMapper;
import com.maicorder.mapper.GameSessionMapper;
import com.maicorder.mapper.PlayRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@CrossOrigin(
        originPatterns = {"http://localhost:8081", "http://swan416.top", "https://swan416.top"},
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private GameSessionMapper gameSessionMapper;

    @Autowired
    private PlayRecordMapper playRecordMapper;

    @Transactional(rollbackFor = Exception.class)
    @PostMapping(produces = "application/json;charset=UTF-8")
    public Result<CheckIn> create(@RequestBody Map<String, Object> payload) {
        try {
            // 1. 取机厅ID（适配前端arcadeId）
            Object arcadeIdObj = payload.get("arcadeId");
            if (arcadeIdObj == null) {
                return Result.badRequest("机厅ID不能为空");
            }
            Long arcadeId = safeGetLong(arcadeIdObj);
            if (arcadeId == null) {
                return Result.badRequest("机厅ID必须为有效数字");
            }

            // 2. 取用户ID
            Object userIdObj = payload.get("userId");
            Long userId = safeGetLong(userIdObj);
            if (userId == null) {
                return Result.badRequest("用户ID不能为空");
            }

            // 3. 构建CheckIn实体
            CheckIn checkIn = new CheckIn();
            checkIn.setArcadeId(arcadeId);
            checkIn.setUserId(userId);

            // ========== 核心修复：从cost嵌套对象取费用字段 ==========
            Map<String, Object> costObj = (Map<String, Object>) payload.get("cost");
            if (costObj != null) {
                checkIn.setCoinCost(safeGetDouble(costObj.get("coin")));       // 对应前端cost.coin
                checkIn.setFoodCost(safeGetDouble(costObj.get("food")));       // 对应前端cost.food
                checkIn.setWaterCost(safeGetDouble(costObj.get("water")));     // 对应前端cost.water
                checkIn.setTransportCost(safeGetDouble(costObj.get("transport"))); // 对应前端cost.transport
            } else {
                // 无费用数据时默认0（避免null）
                checkIn.setCoinCost(0.0);
                checkIn.setFoodCost(0.0);
                checkIn.setWaterCost(0.0);
                checkIn.setTransportCost(0.0);
            }

            // 4. 取日期和备注
            checkIn.setComment((String) payload.get("comment"));
            String dateStr = (String) payload.get("checkInDate");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                try {
                    checkIn.setCheckInTime(LocalDate.parse(dateStr, formatter));
                } catch (DateTimeParseException e) {
                    return Result.badRequest("日期格式错误，需为yyyy-MM-dd");
                }
            } else {
                checkIn.setCheckInTime(LocalDate.now());
            }

            // 5. 插入CheckIn主记录
            int checkInResult = checkInMapper.insert(checkIn);
            if (checkInResult <= 0) {
                return Result.fail(500, "签到主记录插入失败");
            }

            // ========== 适配前端gameSessions：分游戏存储Rating ==========
            List<Map<String, Object>> sessions = (List<Map<String, Object>>) payload.get("gameSessions");
            if (sessions != null && !sessions.isEmpty()) {
                for (Map<String, Object> sessMap : sessions) {
                    GameSession session = new GameSession();
                    session.setCheckInId(checkIn.getId());
                    session.setGameName((String) sessMap.get("gameType")); // 前端传的是gameType，对应后端gameName
                    session.setCurrentRating((String) sessMap.get("currentRating")); // 分游戏存储Rating
                    session.setPcCount(safeGetInteger(sessMap.get("pcCount")));

                    // 插入游戏场次记录
                    int sessionResult = gameSessionMapper.insert(session);
                    if (sessionResult <= 0) {
                        return Result.fail(500, "游戏场次插入失败：" + sessMap.get("gameType"));
                    }

                    // 插入战绩记录
                    List<Map<String, Object>> records = (List<Map<String, Object>>) sessMap.get("records");
                    if (records != null && !records.isEmpty()) {
                        for (Map<String, Object> recMap : records) {
                            PlayRecord rec = new PlayRecord();
                            rec.setGameSessionId(session.getId());
                            rec.setSongName((String) recMap.get("songName"));
                            rec.setScore((String) recMap.get("score"));
                            rec.setClearStatus((String) recMap.get("clearStatus"));

                            int recordResult = playRecordMapper.insert(rec);
                            if (recordResult <= 0) {
                                return Result.fail(500, "战绩插入失败：" + recMap.get("songName"));
                            }
                        }
                    } else {
                        return Result.badRequest("游戏场次[" + sessMap.get("gameType") + "]至少添加一条战绩");
                    }
                }
            } else {
                return Result.badRequest("请至少添加一个游戏场次记录");
            }

            return Result.success(checkIn);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(500, "签到记录插入失败：" + e.getMessage());
        }
    }

    // 辅助方法：安全转换Double（保持不变）
    private Double safeGetDouble(Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // 辅助方法：安全转换Long（保持不变）
    private Long safeGetLong(Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // 辅助方法：安全转换Integer（保持不变）
    private Integer safeGetInteger(Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return 0; // 游玩PC数默认0更合理
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}