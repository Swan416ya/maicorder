package com.maicorder.controller;

import com.maicorder.entity.Arcade;
import com.maicorder.repository.ArcadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arcades")
@CrossOrigin(origins = "*")
public class ArcadeController {

    @Autowired
    private ArcadeRepository arcadeRepository;

    // 获取所有机厅 (前端下拉框要用)
    @GetMapping
    public List<Arcade> list() {
        return arcadeRepository.findAll();
    }

    // 添加新机厅 (比如：上海机皇, 广州大玩家)
    @PostMapping
    public Arcade create(@RequestBody Arcade arcade) {
        return arcadeRepository.save(arcade);
    }
}