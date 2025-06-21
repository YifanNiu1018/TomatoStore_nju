package com.example.tomatomall.repository;

import com.example.tomatomall.po.Stockpile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockpileRepository extends JpaRepository<Stockpile, Integer> {
    Stockpile findByProductId(Integer productId);

    @Modifying
    @Query("UPDATE Stockpile s SET s.amount = s.amount - :quantity, s.version = s.version + 1 WHERE s.productId = :productId AND s.version = :version")
    int reduceStockWithVersion(@Param("productId") Integer productId, @Param("quantity") Integer quantity, @Param("version") Integer version);
}