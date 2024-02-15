package com.example.stock_concurrency.service.mysql.facade;

import org.springframework.stereotype.Component;

import com.example.stock_concurrency.service.mysql.OptimisticLockService;

@Component
public class OptimisticLockStockFacade {

	private final OptimisticLockService optimisticLockService;

	public OptimisticLockStockFacade(OptimisticLockService optimisticLockService) {
		this.optimisticLockService = optimisticLockService;
	}

	public void decrease(Long id, Long quantity) throws InterruptedException {
		while (true) {
			try {
				optimisticLockService.decrease(id, quantity);
				break;
			} catch (Exception e) {
				Thread.sleep(50);
			}
		}
	}
}
