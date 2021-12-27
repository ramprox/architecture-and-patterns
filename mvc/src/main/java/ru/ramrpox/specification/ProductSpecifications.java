package ru.ramrpox.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.ramrpox.entity.Product;

import java.math.BigDecimal;

public class ProductSpecifications {
    public static Specification<Product> productNamePrefix(String prefix) {
        return (root, query, builder) -> builder.like(root.get("name"), "%" + prefix + "%");
    }

    public static Specification<Product> minPrice(BigDecimal minPrice) {
        return (root, query, builder) -> builder.ge(root.get("price"), minPrice);
    }

    public static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return (root, query, builder) -> builder.le(root.get("price"), maxPrice);
    }
}
