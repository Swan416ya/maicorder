package com.maicorder.service.impl;

import com.maicorder.dto.IdResponse;
import com.maicorder.entity.ApiResponse;
import com.maicorder.entity.Best50Data;
import com.maicorder.service.MaimaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class MaimaiServiceImpl implements MaimaiService {

    @Value("${maimaipy-base-url}") String maimaipy_BaseUrl;


    @Autowired private WebClient maimaiClient;
    @Override
    public Mono<Best50Data> QRGetBest50Data(String QR_code) {
        // TODO
        return null;
    }

    @Override
    public Mono<Best50Data> IdGetBest50Data(String ID) {
        return maimaiClient.get()
                .uri("/arcade/bests?credentials=" + ID)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException("400/500 API 请求失败: " + clientResponse.statusCode()))
                )
                .bodyToMono(Best50Data.class)
                .onErrorMap(e -> new RuntimeException("请求失败: " + "/arcade/bests "+" credentials "+ID, e));
    }

    @Override
    public Mono<String> GetUserId(String QR_code) {
        //get arcade/identifiers?QR_Code=QR_code
        System.out.println("start get userId");
        return maimaiClient.get()
                .uri("/arcade/identifiers?code=" + QR_code)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException("400/500 API 请求失败: " + clientResponse.statusCode()))
                )
                .bodyToMono(IdResponse.class)
                .onErrorMap(e -> new RuntimeException("请求失败: " + "/arcade/identifiers "+" code "+QR_code, e))
                .map(IdResponse::getCredentials);

    }
}
