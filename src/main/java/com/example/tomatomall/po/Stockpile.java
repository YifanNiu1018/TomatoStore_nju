package com.example.tomatomall.po;

import com.example.tomatomall.vo.StockpileVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stockpiles")
public class Stockpile {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @Column(name = "product_id")
    private Integer productId;

    @NotNull
    @Column(name = "amount")
    private Integer amount;

    @NotNull
    @Column(name = "frozen")
    private Integer frozen;

    @Version
    private Integer version;

    public StockpileVO toVO() {
        StockpileVO stockpileVO = new StockpileVO();
        stockpileVO.setId(this.id);
        stockpileVO.setProductId(this.product != null ? this.product.getId() : null);
        stockpileVO.setAmount(this.amount);
        stockpileVO.setProductId(this.productId);
        stockpileVO.setFrozen(this.frozen);
        return stockpileVO;
    }
}