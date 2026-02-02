package com.maicorder.controller;

import com.maicorder.entity.CheckIn;
import com.maicorder.entity.GameSession;
import com.maicorder.entity.PlayRecord;
import com.maicorder.mapper.CheckInMapper;
import com.maicorder.mapper.GameSessionMapper;
import com.maicorder.mapper.PlayRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        // 1. 解析基础信息
        // 机厅 ID
        Map<String, Object> arcadeObj = (Map<String, Object>) payload.get("arcade");
        Long arcadeId = Long.valueOf(arcadeObj.get("id").toString());

        CheckIn checkIn = new CheckIn();
        checkIn.setArcadeId(arcadeId);

        // --- 关键修改：设置 UserId ---
        if (payload.get("userId") != null) {
            checkIn.setUserId(Long.valueOf(payload.get("userId").toString()));
        }
        // --------------------------

        checkIn.setComment((String) payload.get("comment"));

        // 费用处理 (使用辅助方法防止空指针)
        checkIn.setCoinCost(safeGetDouble(payload.get("coinCost")));
        checkIn.setFoodCost(safeGetDouble(payload.get("foodCost")));
        checkIn.setWaterCost(safeGetDouble(payload.get("waterCost")));
        checkIn.setTransportCost(safeGetDouble(payload.get("transportCost")));

        // 日期处理
        String dateStr = (String) payload.get("checkInDate");
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            checkIn.setCheckInTime(LocalDate.parse(dateStr));
        } else {
            checkIn.setCheckInTime(LocalDate.now());
        }

        // 保存爷爷 (CheckIn)
        checkInMapper.insert(checkIn);

        // 2. 遍历保存爸爸 (GameSessions)
        List<Map<String, Object>> sessions = (List<Map<String, Object>>) payload.get("gameSessions");
        if (sessions != null) {
            for (Map<String, Object> sessMap : sessions) {
                GameSession session = new GameSession();
                session.setCheckInId(checkIn.getId()); // 连上爷爷
                session.setGameName((String) sessMap.get("gameName"));

                if (sessMap.get("pcCount") != null && !sessMap.get("pcCount").toString().isEmpty()) {
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
                        rec.setGameSessionId(session.getId()); // 连上爸爸
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

    // 辅助方法：安全把 Object 转 Double，处理 null 和 空字符串
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