package com.example.tomatomall.repository;

import com.example.tomatomall.po.COR;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CORRepository extends JpaRepository<COR, Integer> {

    List<COR> findByOrderId(Integer orderId);

    List<COR> findByCartItemId(Integer cartItemId);
}