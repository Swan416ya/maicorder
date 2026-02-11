package com.maicorder.service.impl;

import com.maicorder.entity.Arcade;
import com.maicorder.mapper.ArcadeMapper;
import com.maicorder.service.ArcadeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArcadeServiceImpl extends ServiceImpl<ArcadeMapper, Arcade> implements ArcadeService {

    @Override
    public Map<Long, String> getArcadeNameMapByIds(List<Long> ids) {
        // 1. 空值校验
        if (ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }

        // 2. ID 去重
        List<Long> distinctIds = ids.stream()
                .distinct()
                .collect(Collectors.toList());

        // 3. 直接用 MyBatis-Plus 内置的 baseMapper，无需显式注入
        List<Arcade> arcadeList = baseMapper.listNamesByIds(distinctIds);

        // 4. 转换为 ID→名称的 Map
        Map<Long, String> nameMap = new HashMap<>();
        for (Arcade arcade : arcadeList) {
            String arcadeName = (arcade.getName() == null || arcade.getName().trim().isEmpty())
                    ? "未知机厅"
                    : arcade.getName().trim();
            nameMap.put(arcade.getId(), arcadeName);
        }

        return nameMap;
    }
}