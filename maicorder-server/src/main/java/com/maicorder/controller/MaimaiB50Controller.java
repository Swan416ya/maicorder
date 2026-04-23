package com.maicorder.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maicorder.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/maimai")
public class MaimaiB50Controller {

    private final ObjectMapper objectMapper;

    public MaimaiB50Controller(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping("/b50")
    public Result<Map<String, Object>> fetchB50(@RequestBody Map<String, Object> body) {
        try {
            Object friendCodeObj = body.get("friendCode");
            if (friendCodeObj == null) {
                return Result.badRequest("friendCode 不能为空");
            }

            Long friendCode = parseLong(friendCodeObj);
            if (friendCode == null) {
                return Result.badRequest("friendCode 必须为有效数字");
            }

            Object tokenObj = body.get("accessToken");
            String accessToken = tokenObj == null ? null : String.valueOf(tokenObj).trim();
            if (accessToken == null || accessToken.isEmpty()) {
                return Result.badRequest("accessToken 不能为空");
            }

            String url = "https://maimai.lxns.net/api/v0/maimai/player/" + friendCode + "/bests";

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String bodyStr = response.body();
                if (bodyStr != null && bodyStr.length() > 500) bodyStr = bodyStr.substring(0, 500);
                return Result.fail(response.statusCode(), "落雪 API 请求失败：" + bodyStr);
            }

            JsonNode root = objectMapper.readTree(response.body());
            Map<String, Object> data = objectMapper.convertValue(
                    root,
                    new TypeReference<Map<String, Object>>() {}
            );

            return Result.success(data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.fail(500, "请求被中断");
        } catch (Exception e) {
            return Result.fail(500, "拉取 B50 失败：" + e.getMessage());
        }
    }

    private static Long parseLong(Object value) {
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            String s = String.valueOf(value).trim();
            if (s.isEmpty()) return null;
            return Long.parseLong(s);
        } catch (Exception ignored) {
            return null;
        }
    }
}

