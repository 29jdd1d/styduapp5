package com.studyapp.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyapp.common.result.Result;
import com.studyapp.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Gateway 全局异常处理器
 * 用于处理 WebFlux 响应式环境下的异常
 */
@Slf4j
@Order(-1)  // 优先级高于默认异常处理器
@Component
@RequiredArgsConstructor
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 设置响应头
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 根据异常类型设置响应状态码和消息
        Result<?> result;
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            httpStatus = HttpStatus.valueOf(rse.getStatusCode().value());
            result = Result.error(rse.getStatusCode().value(), rse.getReason());
            log.warn("响应状态异常: {}", rse.getReason());
        } else if (ex instanceof IllegalArgumentException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = Result.error(ResultCode.PARAM_ERROR.getCode(), ex.getMessage());
            log.warn("参数异常: {}", ex.getMessage());
        } else {
            result = Result.error(ResultCode.SYSTEM_ERROR.getCode(), "网关内部错误");
            log.error("Gateway 异常: ", ex);
        }

        response.setStatusCode(httpStatus);

        // 构建响应体
        try {
            String resultJson = objectMapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory()
                    .wrap(resultJson.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("序列化响应结果失败", e);
            return Mono.error(e);
        }
    }
}
