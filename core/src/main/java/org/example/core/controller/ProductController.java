package org.example.core.controller;

import lombok.RequiredArgsConstructor;
import org.example.core.generated.api.ProductApi;
import org.example.core.generated.model.ProductDTO;
import org.example.core.mapper.ProductDtoMapper;
import org.example.domain.dto.ProductDto;
import org.example.domain.service.ProductService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class ProductController implements ProductApi {
    private final ProductService productService;
    private final ProductDtoMapper productDtoMapper;

    @Override
    public void saveProduct(ProductDTO productDTO) {
        ProductDto product = productDtoMapper.toDto(productDTO);
        productService.save(product);
    }
}
