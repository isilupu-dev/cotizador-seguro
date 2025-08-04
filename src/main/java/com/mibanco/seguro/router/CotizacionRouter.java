package com.mibanco.seguro.router;

import com.mibanco.seguro.model.dto.CotizacionRequest;
import com.mibanco.seguro.model.dto.CotizacionResponse;
import com.mibanco.seguro.handler.CotizacionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class CotizacionRouter {
	
	@Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/cotizaciones",
            produces = { "application/json" },
            method = RequestMethod.POST,
            beanClass = CotizacionHandler.class,
            beanMethod = "cotizar",
            operation = @Operation(
                operationId = "cotizarSeguro",
                summary = "Simula la prima de un seguro vehicular",
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CotizacionRequest.class))),
                responses = {
                		@ApiResponse(
                		        responseCode = "201",
                		        description = "Cotización generada",
                		        content = @Content(schema = @Schema(implementation = CotizacionResponse.class))
                		),
                		@ApiResponse(
                		        responseCode = "400",
                		        description = "Solicitud inválida por errores de validación",
                		        content = @Content(schema = @Schema(example = "Solicitud inválida: La marca no puede estar vacía; El modelo no puede estar vacío"))
                		),
                		@ApiResponse(
                		        responseCode = "401",
                		        description = "No autorizado: clave API inválida o ausente",
                		        content = @Content(schema = @Schema(example = "Unauthorized"))
                		),
                		@ApiResponse(
                                responseCode = "500",
                                description = "Error interno del servidor",
                                content = @Content(schema = @Schema(example = "Error interno del servidor"))
                        )
                },
                security = @SecurityRequirement(name = "x-api-key")
            )
        )
    })
	RouterFunction<ServerResponse> routes(CotizacionHandler handler){
		return route(POST("/api/v1/cotizaciones"), handler::cotizar);
	}

}
