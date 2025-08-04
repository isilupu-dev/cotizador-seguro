package com.mibanco.seguro.service;

import com.mibanco.seguro.model.Cotizacion;
import com.mibanco.seguro.model.dto.AjusteAplicado;
import com.mibanco.seguro.model.dto.CotizacionRequest;
import com.mibanco.seguro.model.dto.CotizacionResponse;
import com.mibanco.seguro.repository.CotizacionRepository;
import com.mibanco.seguro.service.cache.CacheKeyGenerator;
import com.mibanco.seguro.service.cache.CotizacionCacheService;
import com.mibanco.seguro.util.TraceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CotizacionServiceImplTest {

    @Mock
    private CotizacionRepository cotizacionRepository;

    @Mock
    private CotizacionCacheService cacheService;

    @Mock
    private CacheKeyGenerator cacheKeyGenerator;

    @InjectMocks
    private CotizacionServiceImpl service;

    private CotizacionRequest request;
    private CotizacionResponse response;

    @BeforeEach
    void setup() {
        request = new CotizacionRequest("Toyota", "Corolla", 2020, "familiar", 30);
        response = new CotizacionResponse(
            BigDecimal.valueOf(500),
            List.of(new AjusteAplicado("Vehiculo posterior a 2015", BigDecimal.valueOf(0.15), BigDecimal.valueOf(75))),
            BigDecimal.valueOf(575)
        );

        TraceContext.getTraceId();
    }

    @Test
    void cotizar_shouldReturnFromCache_whenExists() {
        when(cacheKeyGenerator.generarClaveCotizacion(request)).thenReturn("cotizacion:toyota:corolla:2020:familiar:30");
        when(cacheService.getCotizacion(anyString())).thenReturn(Mono.just(response));

        Mono<CotizacionResponse> result = service.cotizar(request);

        StepVerifier.create(result)
                .expectNext(response)
                .verifyComplete();

        verify(cacheService, never()).saveCotizacion(any(), any());
        verify(cotizacionRepository, never()).save(any());
    }

    @Test
    void cotizar_shouldCalculateAndSave_whenNotInCache() {
        String cacheKey = "cotizacion:toyota:corolla:2020:familiar:30";

        when(cacheKeyGenerator.generarClaveCotizacion(request)).thenReturn(cacheKey);
        when(cacheService.getCotizacion(cacheKey)).thenReturn(Mono.empty());
        when(cotizacionRepository.save(any(Cotizacion.class))).thenReturn(Mono.just(new Cotizacion()));
        when(cacheService.saveCotizacion(eq(cacheKey), any())).thenReturn(Mono.just(true));

        Mono<CotizacionResponse> result = service.cotizar(request);

        StepVerifier.create(result)
                .assertNext(resp -> {
                    assert resp.primaTotal().compareTo(BigDecimal.valueOf(575)) == 0;
                })
                .verifyComplete();

        verify(cotizacionRepository).save(any());
        verify(cacheService).saveCotizacion(eq(cacheKey), any());
    }
    
    @Test
    void cotizar_shouldApplyAjuste_whenTipoUsoIsCarga() {
        CotizacionRequest req = new CotizacionRequest("Toyota", "Hilux", 2010, "carga", 30);

        when(cacheKeyGenerator.generarClaveCotizacion(req)).thenReturn("c1");
        when(cacheService.getCotizacion("c1")).thenReturn(Mono.empty());
        when(cotizacionRepository.save(any())).thenReturn(Mono.just(new Cotizacion()));
        when(cacheService.saveCotizacion(eq("c1"), any())).thenReturn(Mono.just(true));

        StepVerifier.create(service.cotizar(req))
            .assertNext(resp -> {
                assert resp.ajustes().stream().anyMatch(a -> a.motivo().contains("tipo carga"));
            })
            .verifyComplete();
    }

    @Test
    void cotizar_shouldApplyAjuste_whenEdadIsGreaterThan50() {
        CotizacionRequest req = new CotizacionRequest("Toyota", "Yaris", 2010, "familiar", 60);

        when(cacheKeyGenerator.generarClaveCotizacion(req)).thenReturn("c2");
        when(cacheService.getCotizacion("c2")).thenReturn(Mono.empty());
        when(cotizacionRepository.save(any())).thenReturn(Mono.just(new Cotizacion()));
        when(cacheService.saveCotizacion(eq("c2"), any())).thenReturn(Mono.just(true));

        StepVerifier.create(service.cotizar(req))
            .assertNext(resp -> {
                assert resp.ajustes().stream().anyMatch(a -> a.motivo().contains("mayor de 50"));
            })
            .verifyComplete();
    }

    @Test
    void cotizar_shouldApplyAjuste_whenMarcaIsBMW() {
        CotizacionRequest req = new CotizacionRequest("BMW", "X5", 2010, "familiar", 40);

        when(cacheKeyGenerator.generarClaveCotizacion(req)).thenReturn("c3");
        when(cacheService.getCotizacion("c3")).thenReturn(Mono.empty());
        when(cotizacionRepository.save(any())).thenReturn(Mono.just(new Cotizacion()));
        when(cacheService.saveCotizacion(eq("c3"), any())).thenReturn(Mono.just(true));

        StepVerifier.create(service.cotizar(req))
            .assertNext(resp -> {
                assert resp.ajustes().stream().anyMatch(a -> a.motivo().contains("BMW"));
            })
            .verifyComplete();
    }

    @Test
    void cotizar_shouldApplyAjuste_whenMarcaIsAudi() {
        CotizacionRequest req = new CotizacionRequest("Audi", "A3", 2010, "familiar", 40);

        when(cacheKeyGenerator.generarClaveCotizacion(req)).thenReturn("c4");
        when(cacheService.getCotizacion("c4")).thenReturn(Mono.empty());
        when(cotizacionRepository.save(any())).thenReturn(Mono.just(new Cotizacion()));
        when(cacheService.saveCotizacion(eq("c4"), any())).thenReturn(Mono.just(true));

        StepVerifier.create(service.cotizar(req))
            .assertNext(resp -> {
                assert resp.ajustes().stream().anyMatch(a -> a.motivo().contains("Audi"));
            })
            .verifyComplete();
    }

    @Test
    void cotizar_shouldReturnOnlyPrimaBase_whenNoAjustesApply() {
        CotizacionRequest req = new CotizacionRequest("Nissan", "Versa", 2010, "familiar", 30);

        when(cacheKeyGenerator.generarClaveCotizacion(req)).thenReturn("c5");
        when(cacheService.getCotizacion("c5")).thenReturn(Mono.empty());
        when(cotizacionRepository.save(any())).thenReturn(Mono.just(new Cotizacion()));
        when(cacheService.saveCotizacion(eq("c5"), any())).thenReturn(Mono.just(true));

        StepVerifier.create(service.cotizar(req))
            .assertNext(resp -> {
                assert resp.ajustes().isEmpty();
                assert resp.primaTotal().compareTo(BigDecimal.valueOf(500.00)) == 0;
            })
            .verifyComplete();
    }


}
