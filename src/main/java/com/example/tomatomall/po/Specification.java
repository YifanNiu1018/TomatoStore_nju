package com.example.tomatomall.po;

import com.example.tomatomall.vo.SpecificationVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "specifications")
public class Specification {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @NotBlank
    @Column(name = "item")
    private String item;

    @NotBlank
    @Column(name = "value")
    private String value;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public SpecificationVO toVO() {
        SpecificationVO specificationVO = new SpecificationVO();
        specificationVO.setId(this.id);
        specificationVO.setItem(this.item);
        specificationVO.setValue(this.value);
        specificationVO.setProductId(this.product != null ? this.product.getId() : null);
        return specificationVO;
    }
}