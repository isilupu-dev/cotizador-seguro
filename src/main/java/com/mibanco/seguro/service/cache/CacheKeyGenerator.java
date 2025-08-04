package com.mibanco.seguro.service.cache;

import org.springframework.stereotype.Component;

import com.mibanco.seguro.model.dto.CotizacionRequest;

@Component
public class CacheKeyGenerator {

    public String generarClaveCotizacion(CotizacionRequest req) {
        return String.format("cotizacion:%s:%s:%d:%s:%d",
                req.marca().toLowerCase(),
                req.modelo().toLowerCase(),
                req.anio(),
                req.tipoUso().toLowerCase(),
                req.edadConductor());
    }
}
