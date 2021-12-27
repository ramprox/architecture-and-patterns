package ru.ramrpox.service;

import org.springframework.data.domain.Page;
import ru.ramrpox.dto.ProductDto;
import ru.ramrpox.dto.ProductListParams;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<ProductDto> findAll();

    Page<ProductDto> findWithFilter(ProductListParams listParams);

    void save(ProductDto product);

    Optional<ProductDto> findById(Long id);

    void deleteById(Long id);
}
