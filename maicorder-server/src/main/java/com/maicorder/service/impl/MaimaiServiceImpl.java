package com.maicorder.service.impl;

import com.maicorder.dto.IdResponse;
import com.maicorder.entity.ApiResponse;
import com.maicorder.entity.Best50Data;
import com.maicorder.service.MaimaiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.net.URI;
@Service
public class MaimaiServiceImpl implements MaimaiService {
    private static final Logger log = LoggerFactory.getLogger(MaimaiServiceImpl.class);

    @Value("${maimaipy-base-url}") String maimaipy_BaseUrl;


    @Autowired private WebClient maimaiClient;
    @Override
    public Mono<Best50Data> QRGetBest50Data(String QR_code) {
        // TODO
        return null;
    }

    @Override
    public Mono<Best50Data> IdGetBest50Data(String ID) {
        log.info("[MaimaiService] id_b50 start, baseUrl={}, id length={}", maimaipy_BaseUrl, ID == null ? 0 : ID.length());
        System.out.println("输出前的ID "+ID);
        return maimaiClient.get()
                .uri(URI.create(maimaipy_BaseUrl + "/arcade/bests?credentials=" + ID))
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException("400/500 API 请求失败: " + clientResponse.statusCode()))
                )
                .bodyToMono(Best50Data.class)
                .doOnNext(data -> log.info("[MaimaiService] id_b50 success, response type={}",
                        data == null ? "null" : data.getClass().getSimpleName()))
                .doOnError(e -> log.error("[MaimaiService] id_b50 failed, id length={}, error={}",
                        ID == null ? 0 : ID.length(), e.getMessage()))
                .onErrorMap(e -> new RuntimeException("请求失败: " + "/arcade/bests "+" credentials "+ID, e));
    }

    @Override
    public Mono<String> GetUserId(String QR_code) {
        //get arcade/identifiers?QR_Code=QR_code
        System.out.println("start get userId");
        return maimaiClient.get()
                .uri(URI.create(maimaipy_BaseUrl + "/arcade/identifiers?code=" + QR_code))
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
