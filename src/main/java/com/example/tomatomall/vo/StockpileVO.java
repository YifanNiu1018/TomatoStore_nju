package com.example.tomatomall.vo;

import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Stockpile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockpileVO {
    private Integer id;
    private Integer productId;
    private Integer amount;
    private Integer frozen;

    public Stockpile toPO(Product product) {
        Stockpile stockpile = new Stockpile();
        stockpile.setId(this.id);
        stockpile.setProduct(product);
        stockpile.setAmount(this.amount);
        stockpile.setFrozen(this.frozen);
        return stockpile;
    }
}