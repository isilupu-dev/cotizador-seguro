package com.mibanco.seguro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
	    return http
	        .csrf(ServerHttpSecurity.CsrfSpec::disable)
	        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
	        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
	        .authorizeExchange(exchanges -> exchanges
	            .pathMatchers(
	                "/swagger-ui.html",
	                "/swagger-ui/**",
	                "/v3/api-docs/**",
	                "/webjars/**",
	                "/actuator/**",
	                "/"
	            ).permitAll()
	            .anyExchange().permitAll()
	        ).build();
	}
}
