package com.mibanco.seguro.model.dto;

import java.math.BigDecimal;

public record AjusteAplicado(
		
	String motivo,
	BigDecimal porcentaje,
	BigDecimal monto

) {}
