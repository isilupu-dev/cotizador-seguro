package com.mibanco.seguro.service.cache;

import com.mibanco.seguro.model.dto.CotizacionResponse;

import reactor.core.publisher.Mono;

public interface CotizacionCacheService {
	Mono<CotizacionResponse> getCotizacion(String key);
	Mono<Boolean> saveCotizacion(String key, CotizacionResponse response);
}
