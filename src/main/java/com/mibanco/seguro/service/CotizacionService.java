package com.mibanco.seguro.service;

import com.mibanco.seguro.model.dto.CotizacionRequest;
import com.mibanco.seguro.model.dto.CotizacionResponse;

import reactor.core.publisher.Mono;

public interface CotizacionService {
	Mono<CotizacionResponse> cotizar(CotizacionRequest request);
}
