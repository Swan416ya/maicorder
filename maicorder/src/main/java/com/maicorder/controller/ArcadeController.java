package com.maicorder.controller;

import com.maicorder.common.Result;
import com.maicorder.entity.Arcade;
import com.maicorder.mapper.ArcadeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arcades")
public class ArcadeController {

    @Autowired
    private ArcadeMapper arcadeMapper;

    @GetMapping
    public Result<List<Arcade>> list() {
        List<Arcade> arcadeList = arcadeMapper.selectList(null);
        return Result.success(arcadeList);
    }

    @PostMapping
    public Result<Arcade> create(@RequestBody Arcade arcade) {
        arcadeMapper.insert(arcade);
        return Result.success(arcade);
    }
}