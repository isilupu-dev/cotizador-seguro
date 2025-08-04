package com.mibanco.seguro.service.cache;

import java.time.Duration;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import com.mibanco.seguro.model.dto.CotizacionResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RedisCotizacionCacheServiceImpl implements CotizacionCacheService {
	
	private final ReactiveRedisTemplate<String, CotizacionResponse> redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(5);

	@Override
	public Mono<CotizacionResponse> getCotizacion(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	@Override
	public Mono<Boolean> saveCotizacion(String key, CotizacionResponse response) {
		return redisTemplate.opsForValue().set(key, response, TTL);
	}

}
