package com.example.stock_concurrency.service.redis.facade;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.stock_concurrency.service.StockService;

/**
 * Redis  RedissonLock
 * 1. Lettuce는 계속 락 획득을 시도하는 반면에 Redisson은 락 해제가 되었을 때 한 번 혹은 몇번만 시도하기에 레디스에 부하를 줄여준다.
 */
@Component
public class RedissonLockStockFacade {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final RedissonClient redissonClient;
	private final StockService stockService;

	public RedissonLockStockFacade(RedissonClient redissonClient, StockService stockService) {
		this.redissonClient = redissonClient;
		this.stockService = stockService;
	}

	public void decrease(Long id, Long quantity) {
		RLock lock = redissonClient.getLock(id.toString());

		try {
			boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

			if (!available) {
				log.warn("락 획득 실패");
				return;
			}
			stockService.decrease(id, quantity);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}
}
