package org.example.service.service;

import org.example.domain.dto.ProductDto;
import org.example.domain.producer.ProductDeltaEventProducer;
import org.example.domain.producer.ProductFactEventProducer;
import org.example.domain.repository.ProductRepository;
import org.example.service.ModelUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductFactEventProducer factEventProducer;

    @Mock
    private ProductDeltaEventProducer deltaEventProducer;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void saveTest() {
        // Given
        ProductDto productDto = ModelUtils.getProductDto();
        given(productRepository.save(productDto)).willReturn(productDto);
        willDoNothing().given(factEventProducer).sendCreateEvent(productDto);
        willDoNothing().given(deltaEventProducer).sendCreateEvent(productDto);

        // When
        productService.save(productDto);

        // Then
        then(productRepository).should().save(productDto);
        then(factEventProducer).should().sendCreateEvent(productDto);
        then(deltaEventProducer).should().sendCreateEvent(productDto);
    }
}
