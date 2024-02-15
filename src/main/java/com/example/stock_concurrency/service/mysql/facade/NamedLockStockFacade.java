package com.example.stock_concurrency.service.mysql.facade;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock_concurrency.repository.LockRepository;
import com.example.stock_concurrency.service.mysql.NamedLockStockService;
import com.example.stock_concurrency.service.StockService;

@Component
public class NamedLockStockFacade {

	private final LockRepository lockRepository;
	private final NamedLockStockService namedLockStockService;

	public NamedLockStockFacade(LockRepository lockRepository, StockService stockService, NamedLockStockService namedLockStockService) {
		this.lockRepository = lockRepository;
		this.namedLockStockService = namedLockStockService;
	}

	@Transactional
	public void decrease(Long id, Long quantity) {
		try {
			lockRepository.getLock(id.toString());
			namedLockStockService.decrease(id, quantity);
		} finally {
			lockRepository.releaseLock(id.toString());
		}
	}
}
