package com.heima.web.gateway.filter;

import com.heima.web.gateway.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizeFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        if (request.getURI().getPath().contains("/login")) {
            return chain.filter(exchange);
        }

        String token = request.getHeaders().getFirst("token");

        if (StringUtils.isBlank(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
        }

        //判断token是否有效

        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);

            int result = AppJwtUtil.verifyToken(claimsBody);

            if (result == 1 || result == 2) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            //获取用户信息
            Object userId = claimsBody.get("id");

            //存储到header中
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> httpHeaders.add("userId", userId + "")).build();

            //重置请求
            exchange.mutate().request(serverHttpRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //6.放行
        return chain.filter(exchange);

    }
}
