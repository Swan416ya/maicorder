package com.maicorder.controller;

import com.maicorder.entity.CheckIn;
import com.maicorder.entity.PlayRecord;
import com.maicorder.repository.CheckInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/checkins")
@CrossOrigin(origins = "*")
public class CheckInController {

    @Autowired
    private CheckInRepository checkInRepository;

    @GetMapping
    public List<CheckIn> getAll() {
        // 获取列表时，Spring 会自动把关联的 playRecords 也查出来
        return checkInRepository.findAll();
    }

    @PostMapping
    public CheckIn create(@RequestBody CheckIn checkIn) {
        // 1. 自动填写入库时间
        checkIn.setCheckInTime(LocalDateTime.now());

        // 2. 建立双向关联 (这是新手最容易坑的地方！)
        // 前端传来的数据结构是：CheckIn -> 包含 List<PlayRecord>
        // 但是 PlayRecord 对象里面的 "checkIn" 字段是空的。
        // 如果不手动设置，保存到数据库后，PlayRecord 表里的 check_in_id 就是 null。
        List<PlayRecord> records = checkIn.getPlayRecords();
        if (records != null) {
            for (PlayRecord record : records) {
                record.setCheckIn(checkIn); // 告诉儿子，你的爸爸是这个 CheckIn
            }
        }

        // 3. 保存
        // 因为我们在 Entity 里设置了 cascade = CascadeType.ALL
        // 所以只要保存 checkIn，里面的 playRecords 也会自动保存
        return checkInRepository.save(checkIn);
    }
}