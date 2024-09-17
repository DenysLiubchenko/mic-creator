package org.example.producer.producer;

import org.example.domain.constant.EventReason;
import org.example.domain.dto.CartDto;
import org.example.fact.CartFactEvent;
import org.example.producer.ModelUtils;
import org.example.producer.mapper.CartFactEventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class CartFactEventProducerTest {

    @Mock
    private CartFactEventMapper cartFactEventMapper;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private CartFactEventProducerImpl cartFactEventProducer;

    private final String CART_TOPIC = "cart-fact";
    private final CartDto cartDto = ModelUtils.getCartDto();

    @Test
    void sendCreateEventTest() {
        // Given
        CartFactEvent cartFactEvent = ModelUtils.getCartFactEvent(EventReason.CREATE.name());
        given(cartFactEventMapper.toEvent(cartDto, EventReason.CREATE.name())).willReturn(cartFactEvent);

        // When
        cartFactEventProducer.sendCreateEvent(cartDto);

        // Then
        then(cartFactEventMapper).should().toEvent(cartDto, EventReason.CREATE.name());
        then(kafkaTemplate).should().send(CART_TOPIC, String.valueOf(cartFactEvent.getId()), cartFactEvent);
    }

    @Test
    void sendUpdateEventTest() {
        // Given
        CartFactEvent cartFactEvent = ModelUtils.getCartFactEvent(EventReason.UPDATE.name());
        given(cartFactEventMapper.toEvent(cartDto, EventReason.UPDATE.name())).willReturn(cartFactEvent);

        // When
        cartFactEventProducer.sendUpdateEvent(cartDto);

        // Then
        then(cartFactEventMapper).should().toEvent(cartDto, EventReason.UPDATE.name());
        then(kafkaTemplate).should().send(CART_TOPIC, String.valueOf(cartFactEvent.getId()), cartFactEvent);
    }

    @Test
    void sendDeleteEventTest() {
        // Given
        CartFactEvent cartFactEvent = ModelUtils.getCartFactEvent(EventReason.DELETE.name());
        given(cartFactEventMapper.toEvent(cartDto, EventReason.DELETE.name())).willReturn(cartFactEvent);

        // When
        cartFactEventProducer.sendDeleteEvent(cartDto);

        // Then
        then(cartFactEventMapper).should().toEvent(cartDto, EventReason.DELETE.name());
        then(kafkaTemplate).should().send(CART_TOPIC, String.valueOf(cartFactEvent.getId()), cartFactEvent);
    }
}

