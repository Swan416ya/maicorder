package com.maicorder.controller; // 必须是这个包名，和你的项目一致

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.maicorder.common.Result; // 确保你有这个统一返回类
import com.maicorder.entity.User;
import com.maicorder.mapper.UserMapper;
import com.maicorder.utils.JwtUtils; // 确保你有这个JWT工具类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController // 标记为REST接口控制器
@RequestMapping("/api") // 类路径前缀，所以接口路径是/api/xxx
public class UserController {

    @Autowired // 注入UserMapper，用于操作数据库
    private UserMapper userMapper;

    @Autowired // 注入JWT工具类，用于生成Token
    private JwtUtils jwtUtils;


    // 登录接口：POST /api/login（和前端请求的路径完全一致）
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginParam) {
        // 1. 获取前端传的用户名和密码
        String username = loginParam.get("username");
        String password = loginParam.get("password");

        // 2. 校验参数
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return Result.fail(400, "用户名或密码不能为空");
        }

        // 3. 查询数据库中的用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        // 4. 校验用户是否存在
        if (user == null) {
            return Result.fail(401, "用户名不存在");
        }

        // 5. 校验密码（这里假设你存储的是MD5加密后的密码，盐值为"arcade_" + username）
        String salt = "arcade_" + username;
        String encryptPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes(StandardCharsets.UTF_8));
        if (!encryptPassword.equals(user.getPassword())) {
            return Result.fail(401, "密码错误");
        }

        // 6. 生成JWT Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        System.out.println("生成的Token：" + token); // 看后端控制台是否有token输出

        // 7. 构造返回结果
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("token", token);
        // 组装用户信息（只返回安全的字段，不要返回密码）
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        resultData.put("user", userInfo);

        // 8. 返回成功结果
        return Result.success(resultData);
    }


    // 注册接口（可选，前端如果有注册功能需要这个）
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> registerParam) {
        String username = registerParam.get("username");
        String password = registerParam.get("password");
        String email = registerParam.get("email");

        // 校验参数
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return Result.fail(400, "用户名或密码不能为空");
        }

        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        if (userMapper.selectCount(queryWrapper) > 0) {
            return Result.fail(400, "用户名已存在");
        }

        // 密码加密（和登录的盐值逻辑一致）
        String salt = "arcade_" + username;
        String encryptPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes(StandardCharsets.UTF_8));

        // 保存用户到数据库
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptPassword);
        user.setEmail(email);
        userMapper.insert(user);

        return Result.success(null);
    }


    @GetMapping("/get-apikey")
    public Result<String> getApiKey() {
        try {
            // 从 SecurityContext 中获取当前登录用户的认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 检查用户是否已登录
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.fail(401, "用户未登录");
            }

            // 检查是否为匿名用户
            Object principal = authentication.getPrincipal();
            if (principal == null || "anonymousUser".equals(principal.toString())) {
                return Result.fail(401, "用户未登录");
            }

            // 获取用户 ID（JWT 过滤器中设置的是 userId 作为 principal）
            Long userId = null;
            
            // 处理不同类型的 principal
            if (principal instanceof Long) {
                userId = (Long) principal;
            } else if (principal instanceof String) {
                try {
                    // 尝试将 String 转换为 Long
                    userId = Long.parseLong((String) principal);
                } catch (NumberFormatException e) {
                    return Result.fail(401, "无效的用户身份信息");
                }
            } else {
                return Result.fail(401, "无效的用户身份信息");
            }
            
            System.out.println("User ID: " + userId);

            // 查询用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.fail(404, "用户不存在");
            }

            // 获取 API Key
            String apiKey = user.getApiKey();
            if (apiKey == null || apiKey.isEmpty()) {
                return Result.fail(400, "API Key 未设置");
            }

            // 返回 API Key
            return Result.success(apiKey);
        } catch (Exception e) {
//            e.printStackTrace();
            return Result.fail(500, "获取 API Key 失败：" + e.getMessage());
        }

    }
    //TODO 实现更新 API Key 接口
//    @PostMapping("/update-apikey")
//    public Result<Void> updateApiKey(@RequestBody Map<String, String> updateParam) {}
}