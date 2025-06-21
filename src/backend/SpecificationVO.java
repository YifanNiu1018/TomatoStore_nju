package com.example.tomatomall.vo;

import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Specification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SpecificationVO {
    private Integer id;
    private String item;
    private String value;
    private Integer productId;

    public Specification toPO(Product product) {
        Specification specification = new Specification();
        specification.setId(this.id);
        specification.setItem(this.item);
        specification.setValue(this.value);
        specification.setProduct(product);
        return specification;
    }
}