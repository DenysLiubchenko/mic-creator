package org.example.producer.producer;

import org.example.domain.constant.EventReason;
import org.example.domain.dto.DiscountDto;
import org.example.fact.DiscountFactEvent;
import org.example.producer.ModelUtils;
import org.example.producer.mapper.DiscountFactEventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class DiscountFactEventProducerTest {
    @Mock
    private DiscountFactEventMapper discountFactEventMapper;

    @Mock
    private KafkaTemplate<String, Object> discountKafkaTemplate;

    @InjectMocks
    private DiscountFactEventProducerImpl discountFactEventProducer;

    private final String DISCOUNT_TOPIC = "discount-fact";
    private final DiscountDto discountDto = ModelUtils.getDiscountDto();

    @Test
    void sendCreateEventTest() {
        // Given
        DiscountFactEvent discountFactEvent = ModelUtils.getDiscountFactEvent(EventReason.CREATE.name());
        given(discountFactEventMapper.toEvent(discountDto, EventReason.CREATE.name())).willReturn(discountFactEvent);

        // When
        discountFactEventProducer.sendCreateEvent(discountDto);

        // Then
        then(discountFactEventMapper).should().toEvent(discountDto, EventReason.CREATE.name());
        then(discountKafkaTemplate).should().send(DISCOUNT_TOPIC, discountFactEvent.getCode(), discountFactEvent);
    }
}
