package org.example.producer.producer;

import org.example.domain.constant.EventReason;
import org.example.domain.dto.ProductDto;
import org.example.fact.ProductFactEvent;
import org.example.producer.ModelUtils;
import org.example.producer.mapper.ProductFactEventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class ProductFactEventProducerTest {
    @Mock
    private ProductFactEventMapper productFactEventMapper;

    @InjectMocks
    private ProductFactEventProducerImpl productFactEventProducer;

    private final String PRODUCT_TOPIC = "product-fact";
    private final ProductDto productDto = ModelUtils.getProductDto();

    @Test
    void sendCreateEventTest() {
        // Given
        ProductFactEvent productFactEvent = ModelUtils.getProductFactEvent(EventReason.CREATE.name());
        given(productFactEventMapper.toEvent(productDto, EventReason.CREATE.name())).willReturn(productFactEvent);

        // When
        productFactEventProducer.sendCreateEvent(productDto);

        // Then
        then(productFactEventMapper).should().toEvent(productDto, EventReason.CREATE.name());
    }
}
