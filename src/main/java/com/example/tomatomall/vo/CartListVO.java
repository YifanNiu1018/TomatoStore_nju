package com.example.tomatomall.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CartListVO {
    private List<CartVO> items;
    private Integer total;       // 商品种类数
    private BigDecimal totalAmount; // 总金额
}
