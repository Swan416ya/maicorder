package com.maicorder.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.maicorder.common.Result;
import com.maicorder.entity.User;
import com.maicorder.mapper.UserMapper;
import com.maicorder.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证&用户相关接口（RESTful风格）
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    // ==================== 注册接口（RESTful：POST /users 创建用户） ====================
    @PostMapping("/users")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> payload) {
        // 1. 参数校验
        String username = payload.get("username");
        String password = payload.get("password");
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return Result.badRequest("用户名或密码不能为空");
        }

        // 2. 检查用户名是否重复
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        if (userMapper.selectCount(query) > 0) {
            return Result.badRequest("用户名已存在");
        }

        // 3. 密码加盐加密（提升安全性，避免彩虹表破解）
        String salt = "arcade_" + username; // 盐值：用户名唯一，避免相同密码加密后值相同
        String encryptPwd = DigestUtils.md5DigestAsHex((salt + password).getBytes(StandardCharsets.UTF_8));

        // 4. 保存用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptPwd);
        user.setEmail(payload.get("email"));
        userMapper.insert(user);

        // 5. 返回结果（统一Result）
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        return Result.success(data);
    }

    // ==================== 登录接口（RESTful：POST /auth/login 认证登录） ====================
    @PostMapping("/auth/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> payload) {
        // 1. 参数校验
        String username = payload.get("username");
        String password = payload.get("password");
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return Result.badRequest("用户名或密码不能为空");
        }

        // 2. 查询用户
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        User user = userMapper.selectOne(query);
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }

        // 3. 密码校验（和注册时的加盐规则一致）
        String salt = "arcade_" + username;
        String inputEncryptPwd = DigestUtils.md5DigestAsHex((salt + password).getBytes(StandardCharsets.UTF_8));
        if (!inputEncryptPwd.equals(user.getPassword())) {
            return Result.unauthorized("密码错误");
        }

        // 4. 生成JWT Token（RESTful无状态核心）
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        // 5. 构造返回数据（仅返回必要信息，不返回密码）
        Map<String, Object> data = new HashMap<>();
        data.put("token", token); // 前端后续请求需在Header中携带：Authorization: Bearer {token}
        data.put("userInfo", Map.of(
                "userId", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail()
        ));

        // 6. 统一返回Result
        return Result.success(data);
    }
}