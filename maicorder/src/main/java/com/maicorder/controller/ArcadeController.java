package com.maicorder.controller;

import com.maicorder.entity.Arcade;
import com.maicorder.mapper.ArcadeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arcades")
@CrossOrigin(origins = "*")
public class ArcadeController {

    @Autowired
    private ArcadeMapper arcadeMapper; // 名字变了

    @GetMapping
    public List<Arcade> list() {
        return arcadeMapper.selectList(null); // selectList(null) 表示查所有
    }

    @PostMapping
    public Arcade create(@RequestBody Arcade arcade) {
        arcadeMapper.insert(arcade);
        return arcade;
    }
}