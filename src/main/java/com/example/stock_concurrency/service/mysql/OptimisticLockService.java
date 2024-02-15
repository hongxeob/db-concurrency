package com.example.stock_concurrency.service.mysql;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock_concurrency.domain.Stock;
import com.example.stock_concurrency.repository.StockRepository;

@Service
public class OptimisticLockService {

	private final StockRepository stockRepository;

	public OptimisticLockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	/**
	 * OptimisticLock(낙관적락)
	 * 1. 별도의 락을 잡지 않으므로, 비관적 락보다 성능상 이점이 있다.
	 * 2. 업데이트가 실패 했을 때 재시도 로직을 개발자가 직접 작성해줘야 하는 번거로움이 있다.
	 * 3. 충돌이 빈번하게 일어날 것 같은 경우: 비관적락
	 *    반대인 경우는 낙관적락 사용 고려
	 */
	@Transactional
	public void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findByIdWithOptimisticLock(id);

		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}
}
