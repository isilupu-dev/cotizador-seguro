package com.mibanco.seguro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;

import com.mibanco.seguro.model.dto.CotizacionResponse;

@Configuration
public class RedisConfig {
	
	@Bean
    public ReactiveRedisTemplate<String, CotizacionResponse> redisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, CotizacionResponse> context = RedisSerializationContext
            .<String, CotizacionResponse>newSerializationContext(new StringRedisSerializer())
            .value(new Jackson2JsonRedisSerializer<>(CotizacionResponse.class))
            .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

}
