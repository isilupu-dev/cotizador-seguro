package com.mibanco.seguro.handler;

import com.mibanco.seguro.model.dto.CotizacionRequest;
import com.mibanco.seguro.service.CotizacionService;
import com.mibanco.seguro.util.TraceContext;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CotizacionHandler {

    private final CotizacionService cotizacionService;
    private final Validator validator;

    public Mono<ServerResponse> cotizar(ServerRequest request) {
        return request.bodyToMono(CotizacionRequest.class)
            .flatMap(req -> {
                Set<ConstraintViolation<CotizacionRequest>> violations = validator.validate(req);
                if (!violations.isEmpty()) {
                	String errorMsg = violations.stream()
                		    .map(ConstraintViolation::getMessage)
                		    .collect(Collectors.joining("; "));
                    return TraceContext.getTraceId()
                        .flatMap(traceId -> {
                        	log.error("[{}] Error de validación para request {}: {}", traceId, req, errorMsg);
                            return ServerResponse.badRequest().bodyValue("Solicitud inválida: " + errorMsg);
                        });
                }

                return TraceContext.getTraceId()
                    .flatMap(traceId -> cotizacionService.cotizar(req)
                        .doOnSuccess(resp -> log.info("[{}] Cotización procesada exitosamente", traceId))
                        .flatMap(resp -> ServerResponse
                            .created(request.uri())
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(resp))
                    );
            })
            .onErrorResume(e -> {
                return TraceContext.getTraceId()
                    .flatMap(traceId -> {
                        log.error("[{}] Error inesperado al procesar cotización: {}", traceId, e.getMessage(), e);
                        return ServerResponse.status(500).bodyValue("Error interno del servidor");
                    });
            });
    }
}
