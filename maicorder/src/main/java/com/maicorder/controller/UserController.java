package com.maicorder.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.maicorder.entity.User;
import com.maicorder.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils; // Spring自带的加密工具
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    // --- 1. 注册接口 (RESTful: POST /users 代表创建一个用户) ---
    @PostMapping("/users")
    public Map<String, Object> register(@RequestBody Map<String, String> payload) {
        Map<String, Object> result = new HashMap<>();

        String username = payload.get("username");
        String password = payload.get("password");
        String email = payload.get("email");

        // 简单的校验
        if (username == null || password == null) {
            result.put("success", false);
            result.put("message", "用户名或密码不能为空");
            return result;
        }

        // 检查用户名是否已存在
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        if (userMapper.selectCount(query) > 0) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setCreateTime(LocalDateTime.now());

        // 【关键】密码加密！使用 MD5
        // 比如密码是 123456，存进去的就是 e10adc3949ba59abbe56e057f20f883e
        String saltPassword = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        user.setPassword(saltPassword);

        userMapper.insert(user);

        result.put("success", true);
        result.put("message", "注册成功");
        result.put("userId", user.getId()); // 返回ID给前端
        return result;
    }

    // --- 2. 登录接口 ---
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> payload) {
        Map<String, Object> result = new HashMap<>();

        String username = payload.get("username");
        String password = payload.get("password");

        // 1. 根据用户名查用户
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        User user = userMapper.selectOne(query);

        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        // 2. 比对密码
        // 把用户输入的密码也加密一次，看看和数据库里存的一不一样
        String inputPasswordEncrypted = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        if (!inputPasswordEncrypted.equals(user.getPassword())) {
            result.put("success", false);
            result.put("message", "密码错误");
            return result;
        }

        // 3. 登录成功
        result.put("success", true);
        result.put("message", "登录成功");

        // 返回用户信息给前端（注意不要返回密码！）
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());

        result.put("user", userInfo);
        return result;
    }
}