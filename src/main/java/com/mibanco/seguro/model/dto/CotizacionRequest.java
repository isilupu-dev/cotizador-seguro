package com.mibanco.seguro.model.dto;

import jakarta.validation.constraints.*;

public record CotizacionRequest(

    @NotBlank(message = "La marca no puede estar vacía")
    String marca,

    @NotBlank(message = "El modelo no puede estar vacío")
    String modelo,

    @NotNull(message = "El año no puede ser nulo")
    @Min(value = 1900, message = "El año debe ser mayor a 1900")
    Integer anio,

    @NotBlank(message = "El tipo de uso no puede estar vacío")
    String tipoUso,

    @NotNull(message = "La edad del conductor no puede ser nula")
    @Min(value = 18, message = "La edad mínima del conductor es 18")
    @Max(value = 100, message = "La edad máxima del conductor es 100")
    Integer edadConductor

) {}
