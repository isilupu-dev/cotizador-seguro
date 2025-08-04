package com.mibanco.seguro.service.cache;

import com.mibanco.seguro.model.dto.AjusteAplicado;
import com.mibanco.seguro.model.dto.CotizacionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RedisCotizacionCacheServiceImplTest {

    private ReactiveRedisTemplate<String, CotizacionResponse> redisTemplate;
    private ReactiveValueOperations<String, CotizacionResponse> valueOps;
    private RedisCotizacionCacheServiceImpl service;
    private String cacheKey;

    @SuppressWarnings("unchecked")
	@BeforeEach
    void setup() {
    	redisTemplate = (ReactiveRedisTemplate<String, CotizacionResponse>) mock(ReactiveRedisTemplate.class);
    	valueOps = (ReactiveValueOperations<String, CotizacionResponse>) mock(ReactiveValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        service = new RedisCotizacionCacheServiceImpl(redisTemplate);
        cacheKey = "cotizacion:toyota:corolla:2012:carga:30";
    }

    @Test
    void getCotizacion_shouldReturnFromRedis_whenExistsInCache() {
        CotizacionResponse response = new CotizacionResponse(
            BigDecimal.valueOf(500),
            List.of(new AjusteAplicado("Uso tipo carga", BigDecimal.valueOf(0.1), BigDecimal.valueOf(50))),
            BigDecimal.valueOf(550)
        );

        when(valueOps.get(cacheKey)).thenReturn(Mono.just(response));

        Mono<CotizacionResponse> result = service.getCotizacion(cacheKey);

        StepVerifier.create(result)
                .expectNext(response)
                .verifyComplete();

        verify(valueOps).get(cacheKey);
    }

    @Test
    void saveCotizacion_shouldStoreInRedis_whenNotExistsInCache() {
    	
        CotizacionResponse response = new CotizacionResponse(
            BigDecimal.valueOf(500),
            List.of(new AjusteAplicado("Uso tipo carga", BigDecimal.valueOf(0.1), BigDecimal.valueOf(50))),
            BigDecimal.valueOf(550)
        );

        when(valueOps.set(eq(cacheKey), eq(response), any())).thenReturn(Mono.just(true));

        Mono<Boolean> result = service.saveCotizacion(cacheKey, response);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(valueOps).set(eq(cacheKey), eq(response), any());
    }
}
