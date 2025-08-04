package com.mibanco.seguro.service;

import com.mibanco.seguro.model.Cotizacion;
import com.mibanco.seguro.model.dto.AjusteAplicado;
import com.mibanco.seguro.model.dto.CotizacionRequest;
import com.mibanco.seguro.model.dto.CotizacionResponse;
import com.mibanco.seguro.repository.CotizacionRepository;
import com.mibanco.seguro.service.cache.CacheKeyGenerator;
import com.mibanco.seguro.service.cache.CotizacionCacheService;
import com.mibanco.seguro.util.TraceContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final CotizacionCacheService cotizacionCacheService;
    private final CacheKeyGenerator cacheKeyGenerator;

    private static final BigDecimal PRIMA_BASE = BigDecimal.valueOf(500);

    @Override
    public Mono<CotizacionResponse> cotizar(CotizacionRequest request) {
        return TraceContext.getTraceId()
                .flatMap(traceId -> {
                    log.info("[{}] Iniciando cotización para: {}", traceId, request);
			        String cacheKey = cacheKeyGenerator.generarClaveCotizacion(request);
			
			        return cotizacionCacheService.getCotizacion(cacheKey)
			        		.doOnNext(resp -> log.info("[{}] Cotización encontrada en cache", traceId))
			                .switchIfEmpty(Mono.defer(() -> {
			                	log.info("[{}] Cotización no encontrada en cache, calculando...", traceId);
		                        return calcularYGuardar(request, cacheKey, traceId);
			                }));
                });
    }

    private Mono<CotizacionResponse> calcularYGuardar(CotizacionRequest request, String cacheKey, String traceId ) {
        CotizacionResponse response = calcularCotizacion(request);
        Cotizacion entidad = mapToEntity(request, response);

        return cotizacionRepository.save(entidad)
                .doOnSuccess(v -> log.info("[{}] Cotización guardada en MongoDB", traceId))
                .then(cotizacionCacheService.saveCotizacion(cacheKey, response))
                .doOnSuccess(v -> log.info("[{}] Cotización guardada en Redis con TTL de 5 minutos", traceId))
                .thenReturn(response);
    }

    private CotizacionResponse calcularCotizacion(CotizacionRequest request) {
        List<AjusteAplicado> ajustes = new ArrayList<>();

        if (request.anio() > 2015) {
            ajustes.add(ajuste("Vehiculo posterior a 2015", BigDecimal.valueOf(0.15)));
        }
        if ("carga".equalsIgnoreCase(request.tipoUso())) {
            ajustes.add(ajuste("Uso tipo carga", BigDecimal.valueOf(0.10)));
        }
        if (request.edadConductor() > 50) {
            ajustes.add(ajuste("Conductor mayor de 50 años", BigDecimal.valueOf(-0.05)));
        }
        if ("bmw".equalsIgnoreCase(request.marca())) {
            ajustes.add(ajuste("Marca BMW", BigDecimal.valueOf(0.20)));
        } else if ("audi".equalsIgnoreCase(request.marca())) {
            ajustes.add(ajuste("Marca Audi", BigDecimal.valueOf(0.10)));
        }

        BigDecimal totalAjustes = ajustes.stream()
                .map(AjusteAplicado::monto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal primaTotal = PRIMA_BASE.add(totalAjustes).setScale(2, RoundingMode.HALF_UP);

        return new CotizacionResponse(
                PRIMA_BASE.setScale(2, RoundingMode.HALF_UP),
                ajustes,
                primaTotal
        );
    }

    private AjusteAplicado ajuste(String motivo, BigDecimal porcentaje) {
        BigDecimal monto = PRIMA_BASE.multiply(porcentaje);
        return new AjusteAplicado(motivo, porcentaje, monto);
    }

    private Cotizacion mapToEntity(CotizacionRequest request, CotizacionResponse response) {
        return Cotizacion.builder()
                .marca(request.marca())
                .modelo(request.modelo())
                .anio(request.anio())
                .tipoUso(request.tipoUso())
                .edadConductor(request.edadConductor())
                .primabase(response.primaBase())
                .ajustes(response.ajustes())
                .primaTotal(response.primaTotal())
                .build();
    }
}
