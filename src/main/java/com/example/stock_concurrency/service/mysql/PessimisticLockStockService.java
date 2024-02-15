package com.example.stock_concurrency.service.mysql;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock_concurrency.domain.Stock;
import com.example.stock_concurrency.repository.StockRepository;

@Service
public class PessimisticLockStockService {

	private final StockRepository stockRepository;

	public PessimisticLockStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	/**
	 * PessimisticLock(비관적락)
	 * 1. 충돌이 빈번하게 일어난다면 낙관적 락보다 성능이 좋을 수 있다.
	 * 2. 락을 통해 업데이트를 제어하기 때문에 데이터 정합성이 보장됨.
	 * 3. 별도의 락을 잡기 때문에 성능 감소가 있을 수 있다.
	 */
	@Transactional
	public void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findByIdWithPessimisticLock(id);

		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}
}
