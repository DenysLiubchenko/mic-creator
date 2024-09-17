package org.example.service.service;

import org.example.domain.dto.DiscountDto;
import org.example.domain.producer.DiscountDeltaEventProducer;
import org.example.domain.producer.DiscountFactEventProducer;
import org.example.domain.repository.DiscountRepository;
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
public class DiscountServiceTest {
    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private DiscountFactEventProducer factEventProducer;

    @Mock
    private DiscountDeltaEventProducer deltaEventProducer;

    @InjectMocks
    private DiscountServiceImpl discountService;

    @Test
    void saveTest() {
        // Given
        DiscountDto discountDto = ModelUtils.getDiscountDto();
        given(discountRepository.save(discountDto)).willReturn(discountDto);
        willDoNothing().given(factEventProducer).sendCreateEvent(discountDto);
        willDoNothing().given(deltaEventProducer).sendCreateEvent(discountDto);

        // When
        discountService.save(discountDto);

        // Then
        then(discountRepository).should().save(discountDto);
        then(factEventProducer).should().sendCreateEvent(discountDto);
        then(deltaEventProducer).should().sendCreateEvent(discountDto);
    }
}
