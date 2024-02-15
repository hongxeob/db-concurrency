package com.example.stock_concurrency.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.stock_concurrency.domain.Stock;
import com.example.stock_concurrency.repository.StockRepository;

@SpringBootTest
class StockServiceTest {

	@Autowired
	private StockService stockService;

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
}
