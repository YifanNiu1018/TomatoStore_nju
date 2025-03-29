package com.example.tomatomall.po;

import com.example.tomatomall.vo.ProductVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @NotBlank
    @Column(name = "title")
    private String title;

    @NotNull
    @Min(0)
    @Column(name = "price")
    private BigDecimal price;

    @NotNull
    @Min(0)
    @Max(10)
    @Column(name = "rate")
    private Double rate;

    @Column(name = "description")
    private String description;

    @Column(name = "cover")
    private String cover;

    @Column(name = "detail")
    private String detail;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Specification> specifications;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Stockpile stockpile;

    public ProductVO toVO() {
        ProductVO productVO = new ProductVO();
        productVO.setId(this.id);
        productVO.setTitle(this.title);
        productVO.setPrice(this.price);
        productVO.setRate(this.rate);
        productVO.setDescription(this.description);
        productVO.setCover(this.cover);
        productVO.setDetail(this.detail);
        return productVO;
    }
}