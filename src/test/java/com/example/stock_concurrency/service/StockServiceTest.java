package com.example.stock_concurrency.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.stock_concurrency.domain.Stock;
import com.example.stock_concurrency.facade.OptimisticLockStockFacade;
import com.example.stock_concurrency.repository.StockRepository;

@SpringBootTest
class StockServiceTest {

	@Autowired
	private StockService stockService;

	@Autowired
	private SynchronizedStockService synchronizedStockService;

	@Autowired
	private PessimisticLockStockService pessimisticLockStockService;

	@Autowired
	private OptimisticLockStockFacade optimisticLockStockFacade;

	@Autowired
	private StockRepository stockRepository;

	@BeforeEach
	void setUp() {
		stockRepository.saveAndFlush(new Stock(1L, 100L));
	}

	@AfterEach
	void after() {
		stockRepository.deleteAll();
	}

	@Test
	@DisplayName("재고감소")
	void 재고감소() throws Exception {

		//given
		stockService.decrease(1L, 1L);

		//when
		Stock stock = stockRepository.findById(1L).orElseThrow();

		//then
		assertThat(stock.getQuantity()).isEqualTo(99);
	}

	@Test
	@DisplayName("동시에 100개 요청 - Normal")
	void normalDecreaseTest() throws Exception {

		//given
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		//when
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					stockService.decrease(1L, 1L);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		Stock stock = stockRepository.findById(1L).orElseThrow();

		//then
		assertThat(stock.getQuantity()).isEqualTo(0);
	}

	@Test
	@DisplayName("동시에 100개 요청 - Synchronized")
	void synchronizedDecreaseTest() throws Exception {

		//given
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		//when
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					synchronizedStockService.decrease(1L, 1L);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		Stock stock = stockRepository.findById(1L).orElseThrow();

		//then
		assertThat(stock.getQuantity()).isEqualTo(0);
	}

	@Test
	@DisplayName("동시에 100개 요청 - pessimisticLock")
	void pessimisticLockDecreaseTest() throws Exception {

		//given
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		//when
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					pessimisticLockStockService.decrease(1L, 1L);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		Stock stock = stockRepository.findById(1L).orElseThrow();

		//then
		assertThat(stock.getQuantity()).isEqualTo(0);
	}

	@Test
	@DisplayName("동시에 100개 요청 - optimisticLock")
	void optimisticLockDecreaseTest() throws Exception {

		//given
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		//when
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					optimisticLockStockFacade.decrease(1L, 1L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		Stock stock = stockRepository.findById(1L).orElseThrow();

		//then
		assertThat(stock.getQuantity()).isEqualTo(0);
	}
}
