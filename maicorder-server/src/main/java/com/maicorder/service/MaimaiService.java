package com.maicorder.service;

import com.maicorder.entity.Best50Data;
import reactor.core.publisher.Mono;

public interface MaimaiService {
    Mono<Best50Data> QRGetBest50Data(String QR_Code);
    Mono<Best50Data> IdGetBest50Data(String ID);
    Mono<String> GetUserId(String QR_Code);
}
