package com.example.stock_concurrency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.example.stock_concurrency.domain.Stock;

import jakarta.persistence.LockModeType;

public interface StockRepository extends JpaRepository<Stock, Long> {

	//비관적락
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select s from Stock s where s.id=:id")
	Stock findByIdWithPessimisticLock(Long id);

	//낙관적락
	@Lock(LockModeType.OPTIMISTIC)
	@Query("select s from Stock s where s.id=:id")
	Stock findByIdWithOptimisticLock(Long id);
}
