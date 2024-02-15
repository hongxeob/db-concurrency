package com.example.stock_concurrency.service.redis.facade;

import org.springframework.stereotype.Component;

import com.example.stock_concurrency.repository.RedisLockRepository;
import com.example.stock_concurrency.service.StockService;

/**
 * Redis LettuceLock
 * 1. 간단하게 구현이 가능하다.
 * 2. 스핀락(spin Lock) 기반이므로 레디스에 부하를 줄 수 있다.
 * -> 그래서 Thread Sleep 등을 통하여 락 획득 재시도간 간격을 둬야한다.
 */
@Component
public class LettuceLockStockFacade {

	private final RedisLockRepository redisLockRepository;
	private final StockService stockService;

	public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
		this.redisLockRepository = redisLockRepository;
		this.stockService = stockService;
	}

	public void decrease(Long id, Long quantity) throws InterruptedException {
		while (!redisLockRepository.lock(id)) {
			Thread.sleep(100);
		}

		try {
			stockService.decrease(id, quantity);
		} finally {
			redisLockRepository.unLock(id);
		}
	}
}
