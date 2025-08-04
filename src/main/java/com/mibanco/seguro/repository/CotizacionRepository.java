package com.mibanco.seguro.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.mibanco.seguro.model.Cotizacion;

@Repository
public interface CotizacionRepository extends ReactiveMongoRepository<Cotizacion, String> {

}
