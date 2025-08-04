package com.mibanco.seguro.service.cache;

import com.mibanco.seguro.model.dto.CotizacionRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CacheKeyGeneratorTest {

    @Test
    void generarClaveCotizacion_shouldGenerateExpectedKey() {
        CotizacionRequest req = new CotizacionRequest("Toyota", "Corolla", 2020, "Familiar", 30);
        CacheKeyGenerator generator = new CacheKeyGenerator();
        String result = generator.generarClaveCotizacion(req);
        assertEquals("cotizacion:toyota:corolla:2020:familiar:30", result);
    }
}
