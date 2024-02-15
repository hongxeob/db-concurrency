package com.example.stock_concurrency.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.stock_concurrency.domain.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
