package com.maicorder.controller; // 必须是这个包名，和你的项目一致

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.maicorder.common.Result; // 确保你有这个统一返回类
import com.maicorder.entity.User;
import com.maicorder.mapper.UserMapper;
import com.maicorder.utils.JwtUtils; // 确保你有这个JWT工具类
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

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
        
        // 解析Token并输出内容
        try {
            Claims claims = jwtUtils.parseToken(token);
            System.out.println("解析Token结果：");
            System.out.println("  userId: " + claims.get("userId"));
            System.out.println("  username: " + claims.get("username"));
            System.out.println("  签发时间: " + claims.getIssuedAt());
            System.out.println("  过期时间: " + claims.getExpiration());
        } catch (Exception e) {
            System.out.println("解析Token失败：" + e.getMessage());
        }

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
    public Result<String> getApiKey(HttpServletRequest request) {
        try {
            System.out.println("in try 1");
            // 从 SecurityContext 中获取当前登录用户的认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // 从请求头中获取token并解析
            String authorizationHeader = request.getHeader("Authorization");
            System.out.println("Authorization: " + authorizationHeader);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                System.out.println("token: " + token);
                try {
                    Claims claims = jwtUtils.parseToken(token);
                    System.out.println("更新API Key时解析Token结果：");
                    System.out.println("  userId: " + claims.get("userId"));
                    System.out.println("  username: " + claims.get("username"));
                    System.out.println("  签发时间: " + claims.getIssuedAt());
                    System.out.println("  过期时间: " + claims.getExpiration());                } catch (Exception e) {
                    System.out.println("更新API Key时解析Token失败：" + e.getMessage());
                }
            }
            
            // 检查用户是否已登录
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.fail(401, "用户未登录");
            }

            // 检查是否为匿名用户
            Object principal = authentication.getPrincipal();
            if (principal == null || "anonymousUser".equals(principal.toString())) {
                return Result.fail(402, "用户未登录");
            }

            // 获取用户 ID（JWT 过滤器中设置的是 userId 作为 principal）
            Long userId = (Long) principal;
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


    // 更新 API Key 接口
//    @PostMapping("/update-apikey")
//    public Result<String> updateApiKey(@RequestBody Map<String, String> updateParam, HttpServletRequest request) {
//        String apiKey = updateParam.get("apiKey");
//        System.out.println(apiKey);
//
//        // TODO 校验参数
//        if (apiKey == null || apiKey.trim().isEmpty()) {
//            return Result.fail(400, "API Key 不能为空");
//        }
//        //修改用户apiKey
//        // 从 SecurityContext 中获取当前登录用户的认证信息
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        // 检查用户是否已登录
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return Result.fail(401, "用户未登录");
//        }
//        // 检查是否为匿名用户
//        Object principal = authentication.getPrincipal();
//        if (principal == null || "anonymousUser".equals(principal.toString())) {
//            return Result.fail(402, "用户未登录");
//        }
//        // 获取用户 ID（JWT 过滤器中设置的是 userId 作为 principal）
//        Long userId = (Long) principal;
//        System.out.println("User ID: " + userId);
//        // 查询用户信息
//        User user = userMapper.selectById(userId);
//        if (user == null) {
//            return Result.fail(404, "用户不存在");
//        }
//        // 更新 API Key
//        user.setApiKey(apiKey);
//        userMapper.updateById(user);
//        return Result.success(apiKey);
//    }

    // 更新用户信息接口
    @PostMapping("/update-user")
    public Result<Map<String, Object>> updateUser(@RequestBody Map<String, String> updateParam) {
        // 从 SecurityContext 中获取当前登录用户的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 检查用户是否已登录
        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.fail(401, "用户未登录");
        }
        // 检查是否为匿名用户
        Object principal = authentication.getPrincipal();
        if (principal == null || "anonymousUser".equals(principal.toString())) {
            return Result.fail(402, "用户未登录");
        }
        // 获取用户 ID
        Long userId = (Long) principal;
        System.out.println("Update User ID: " + userId);
        
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        if (updateParam.containsKey("email")) {
            String email = updateParam.get("email");
            if (email != null) {
                user.setEmail(email);
            }
        }
//
        if (updateParam.containsKey("password")) {
            String password = updateParam.get("password");
            if (password != null && !password.trim().isEmpty()) {
                // 密码加密
                String salt = "arcade_" + user.getUsername();
                String encryptPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes(StandardCharsets.UTF_8));
                user.setPassword(encryptPassword);
            }
        }
//
        // 检查 apiKey（支持 apikey 和 apiKey 两种键名）
        String apiKey = null;
        if (updateParam.containsKey("apiKey")) {
            apiKey = updateParam.get("apiKey");
        } else if (updateParam.containsKey("apikey")) {
            apiKey = updateParam.get("apikey");
        }
        
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            System.out.println(apiKey);
            user.setApiKey(apiKey);
        }
        
        // 保存更新
        userMapper.updateById(user);
//
//        // 构造返回结果
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("apiKey", user.getApiKey());

        return Result.success(userInfo);

    }
}