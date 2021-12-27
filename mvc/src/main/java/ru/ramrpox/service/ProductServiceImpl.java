package ru.ramrpox.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ramrpox.dto.ProductDto;
import ru.ramrpox.dto.ProductListParams;
import ru.ramrpox.entity.Product;
import ru.ramrpox.repository.ProductRepository;
import ru.ramrpox.specification.ProductSpecifications;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDto> findAll() {
        return productRepository.findAll().stream()
                .map(product -> new ProductDto(product.getId(),
                        product.getName(),
                        product.getPrice(), product.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDto> findWithFilter(ProductListParams listParams) {
        Specification<Product> spec = Specification.where(null);
        String productNameFilter = listParams.getProductName();
        BigDecimal minCostFilter = listParams.getMinPrice();
        BigDecimal maxCostFilter = listParams.getMaxPrice();
        String sortBy = listParams.getSortBy();
        if(productNameFilter != null && !productNameFilter.isEmpty()) {
            spec = spec.and(ProductSpecifications.productNamePrefix(productNameFilter));
        }
        if(minCostFilter != null) {
            spec = spec.and(ProductSpecifications.minPrice(minCostFilter));
        }
        if(maxCostFilter != null) {
            spec = spec.and(ProductSpecifications.maxPrice(maxCostFilter));
        }
        Sort sortedBy = sortBy != null && !sortBy.isEmpty() ? Sort.by(sortBy) : Sort.by("id");
        sortedBy = "desc".equals(listParams.getDirection()) ? sortedBy.descending() : sortedBy.ascending();

        return productRepository.findAll(spec, PageRequest.of(Optional.ofNullable(listParams.getPage()).orElse(1) - 1,
                Optional.ofNullable(listParams.getSize()).orElse(10), sortedBy))
                .map(product -> new ProductDto(product.getId(), product.getName(), product.getPrice(),
                        product.getDescription()));
    }

    @Override
    @Transactional
    public void save(ProductDto productDto) {
        Product product = new Product(productDto.getId(),
                productDto.getName(), productDto.getPrice(), productDto.getDescription());
        productRepository.save(product);
    }

    @Override
    public Optional<ProductDto> findById(Long id) {
        return productRepository.findById(id)
                .map(product -> new ProductDto(product.getId(),
                        product.getName(),
                        product.getPrice(), product.getDescription()));
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
