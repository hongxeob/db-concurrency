package com.example.stock_concurrency.service.mysql;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock_concurrency.domain.Stock;
import com.example.stock_concurrency.repository.StockRepository;

/**
 * Named Lock 은 이름을 가진 메타데이터 락.
 * 해제할 때까지 다른 세션은 이 락을 획득 할 수 없다.
 * 따로 명령어로 락을 해제시키거나, 락의 선점 시간이 끝나야 해제된다.
 * MySQL 에서는 get_lock 명령어로 락 획득 가능, release_lock 명령어로 락 해제 가능
 */
@Service
public class NamedLockStockService {

	private final StockRepository stockRepository;

	public NamedLockStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	/**
	 * NamedLock(낙관적락)
	 * 1. 주로 분산락을 구현할 떄 사용
	 * 2. 타임아웃을 손쉽게 구현 가능
	 * 데이터 삽입시에 정합성을 맞춰야 하는 경우에도 사용가능
	 * 하지만 트랜잭션 종료시 락해제 세션 관리등을 잘해줘야 하므로 주의해서 사용해야한다.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findByIdWithOptimisticLock(id);

		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}
}
