package com.mibanco.seguro.config;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
public class TraceIdWebFilter implements WebFilter {

    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = UUID.randomUUID().toString();
        return chain.filter(exchange)
                .contextWrite(Context.of(TRACE_ID_KEY, traceId));
    }
}
