package com.maicorder.controller;

import com.maicorder.common.ApiResponse;
import com.maicorder.entity.Best50Data;
import com.maicorder.service.MaimaiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;



@RestController
@RequestMapping({"/maimai", "/api/maimai"})
public class MaimaiController {
    private static final Logger log = LoggerFactory.getLogger(MaimaiController.class);

    @Autowired private MaimaiService maimaiService;

    @GetMapping("/qr_b50")
    public Mono<ApiResponse<Best50Data>> b50(@RequestParam String QR_code) {
        return maimaiService.QRGetBest50Data(QR_code)
                .map(data -> ApiResponse.success("/maimai/b50", data));
    }

    @GetMapping("/id_b50")
    public Mono<ApiResponse<Best50Data>> idB50(@RequestParam String id) {
        log.info("[MaimaiController] GET /id_b50 called, id length={}", id == null ? 0 : id.length());
        
        // 处理ping请求
        if ("__ping__".equals(id)) {
            log.info("[MaimaiController] Handling ping request");
            return Mono.just(ApiResponse.success("/maimai/id_b50", new Best50Data()));
        }
        
        return maimaiService.IdGetBest50Data(id)
                .map(data -> ApiResponse.success("/maimai/id_b50", data));
    }

    @GetMapping("/id")
    public Mono<ApiResponse<String>> id(@RequestParam String QR_code) {
    return maimaiService.GetUserId(QR_code)
                .map(data -> ApiResponse.success("/maimai/id", data));
    }
}
