package com.mibanco.seguro.util;

import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

public class TraceContext {

    private static final String TRACE_ID_KEY = "traceId";

    public static Mono<String> getTraceId() {
        return Mono.deferContextual(ctx -> Mono.justOrEmpty(getTraceIdFromContext(ctx)));
    }

    private static String getTraceIdFromContext(ContextView ctx) {
        return ctx.hasKey(TRACE_ID_KEY) ? ctx.get(TRACE_ID_KEY) : "NO_TRACE_ID";
    }
}
