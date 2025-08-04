package com.mibanco.seguro.model;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mibanco.seguro.model.dto.AjusteAplicado;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Document(collection = "cotizaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cotizacion {
	
	@Id
	private String id;
	
	private String marca;	
	private String modelo;	
	private Integer anio;	
	private String tipoUso;	
	private Integer edadConductor;
	
	private BigDecimal primabase;	
	private List<AjusteAplicado> ajustes;
	private BigDecimal primaTotal;	
	
	

}
