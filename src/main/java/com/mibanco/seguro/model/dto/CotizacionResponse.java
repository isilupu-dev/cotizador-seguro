package com.mibanco.seguro.model.dto;

import java.math.BigDecimal;
import java.util.List;

public record CotizacionResponse(
	
	BigDecimal primaBase,
	List<AjusteAplicado> ajustes,
	BigDecimal primaTotal
		
) {}
