package com.example.stock_concurrency.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock_concurrency.domain.Stock;
import com.example.stock_concurrency.repository.StockRepository;

@Service
public class SynchronizedStockService {

	private final StockRepository stockRepository;

	public SynchronizedStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	/**
	 * synchronized 문제점들
	 * 1. 정합성이 맞지 않다 - @Transactional이 프록시 형태로 실행되어 메서드가 종료될 때 데이터 커밋이 일어난다.
	 * 1-1. synchronized된 메서드를 벗어나, 하나의 트랜잭션이 커밋되기 전에 다른 스레드가 메서드에 접근하여 동작을 수행하면
	 * DB는 아직 업데이트가 되지 않았기 때문에 정항이 안맞다.
	 * 즉 synchronized는 한 프로세스 내에서만 스레드의 접근 제한을 보장한다.
	 * 따라서 2개 이상의 서버(프로세스)에서는 갱신 손실 문제를 해결할 수 없다.
	 * - @Transactional을 사용하지 않으면 정합성이 깨지지 않는다.
	 */
	@Transactional
	public synchronized void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findById(id)
			.orElseThrow();

		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}
}
