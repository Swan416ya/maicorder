package com.maicorder.controller;

import com.maicorder.entity.CheckIn;
import com.maicorder.entity.GameSession;
import com.maicorder.entity.PlayRecord;
import com.maicorder.mapper.CheckInMapper;
import com.maicorder.mapper.GameSessionMapper;
import com.maicorder.mapper.PlayRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate; // 注意是 LocalDate
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkins")
@CrossOrigin(origins = "*")
public class CheckInController {

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private GameSessionMapper gameSessionMapper;

    @Autowired
    private PlayRecordMapper playRecordMapper;

    @PostMapping
    public CheckIn create(@RequestBody Map<String, Object> payload) {
        // 1. 基础信息
        Map<String, Object> arcadeObj = (Map<String, Object>) payload.get("arcade");
        Long arcadeId = Long.valueOf(arcadeObj.get("id").toString());

        CheckIn checkIn = new CheckIn();
        checkIn.setArcadeId(arcadeId);
        checkIn.setComment((String) payload.get("comment"));

        // --- 费用处理 ---
        checkIn.setCoinCost(safeGetDouble(payload.get("coinCost")));
        checkIn.setFoodCost(safeGetDouble(payload.get("foodCost")));
        checkIn.setWaterCost(safeGetDouble(payload.get("waterCost")));
        checkIn.setTransportCost(safeGetDouble(payload.get("transportCost")));

        // --- 日期处理 (新逻辑) ---
        String dateStr = (String) payload.get("checkInDate");
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            // 前端传了日期，就用前端的 (格式 YYYY-MM-DD，LocalDate可以直接解析)
            checkIn.setCheckInTime(LocalDate.parse(dateStr));
        } else {
            // 没传就默认今天
            checkIn.setCheckInTime(LocalDate.now());
        }

        // 保存爷爷
        checkInMapper.insert(checkIn);

        // 2. 遍历保存爸爸 (GameSessions)
        List<Map<String, Object>> sessions = (List<Map<String, Object>>) payload.get("gameSessions");
        if (sessions != null) {
            for (Map<String, Object> sessMap : sessions) {
                GameSession session = new GameSession();
                session.setCheckInId(checkIn.getId());
                session.setGameName((String) sessMap.get("gameName"));

                if (sessMap.get("pcCount") != null) {
                    session.setPcCount(Integer.valueOf(sessMap.get("pcCount").toString()));
                }

                if (sessMap.get("currentRating") != null) {
                    session.setCurrentRating(sessMap.get("currentRating").toString());
                }

                gameSessionMapper.insert(session);

                // 3. 遍历保存孙子 (PlayRecords)
                List<Map<String, Object>> records = (List<Map<String, Object>>) sessMap.get("records");
                if (records != null) {
                    for (Map<String, Object> recMap : records) {
                        PlayRecord rec = new PlayRecord();
                        rec.setGameSessionId(session.getId());
                        rec.setSongName((String) recMap.get("songName"));
                        rec.setScore((String) recMap.get("score"));
                        rec.setClearStatus((String) recMap.get("clearStatus"));

                        playRecordMapper.insert(rec);
                    }
                }
            }
        }
        return checkIn;
    }

    // 辅助方法：安全转Double
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
}