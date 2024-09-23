package org.example.producer.producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.CartDto;
import org.example.fact.CartFactEvent;
import org.example.producer.ModelUtils;
import org.example.producer.adapter.OutBoxRepository;
import org.example.producer.entity.OutBoxEntity;
import org.example.producer.mapper.CartFactEventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class CartFactEventProducerTest {

    @Mock
    private CartFactEventMapper cartFactEventMapper;

    @Mock
    private OutBoxRepository outBoxRepository;

    @Mock
    private KafkaAvroSerializer kafkaAvroSerializer;

    @InjectMocks
    private CartFactEventProducerImpl cartFactEventProducer;

    private final String CART_TOPIC = "cart-fact";
    private final CartDto cartDto = ModelUtils.getCartDto();
    private final byte[] serializedEvent = new byte[]{1, 2, 3};

    @Test
    void sendCreateEventTest() {
        // Given
        CartFactEvent cartFactEvent = ModelUtils.getCartFactEvent(EventReason.CREATE.name());

        given(cartFactEventMapper.toEvent(cartDto, EventReason.CREATE.name())).willReturn(cartFactEvent);
        given(kafkaAvroSerializer.serialize(CART_TOPIC, cartFactEvent)).willReturn(serializedEvent);

        // When
        cartFactEventProducer.sendCreateEvent(cartDto);

        // Then
        then(cartFactEventMapper).should().toEvent(cartDto, EventReason.CREATE.name());
        then(kafkaAvroSerializer).should().serialize(CART_TOPIC, cartFactEvent);

        then(outBoxRepository).should().save(OutBoxEntity.builder()
                .key(String.valueOf(cartFactEvent.getId()))
                .destination(CART_TOPIC)
                .payload(serializedEvent)
                .build());
    }

    @Test
    void sendUpdateEventTest() {
        // Given
        CartFactEvent cartFactEvent = ModelUtils.getCartFactEvent(EventReason.UPDATE.name());

        given(cartFactEventMapper.toEvent(cartDto, EventReason.UPDATE.name())).willReturn(cartFactEvent);
        given(kafkaAvroSerializer.serialize(CART_TOPIC, cartFactEvent)).willReturn(serializedEvent);

        // When
        cartFactEventProducer.sendUpdateEvent(cartDto);

        // Then
        then(cartFactEventMapper).should().toEvent(cartDto, EventReason.UPDATE.name());
        then(kafkaAvroSerializer).should().serialize(CART_TOPIC, cartFactEvent);

        then(outBoxRepository).should().save(OutBoxEntity.builder()
                .key(String.valueOf(cartFactEvent.getId()))
                .destination(CART_TOPIC)
                .payload(serializedEvent)
                .build());
    }

    @Test
    void sendDeleteEventTest() {
        // Given
        CartFactEvent cartFactEvent = ModelUtils.getCartFactEvent(EventReason.DELETE.name());

        given(cartFactEventMapper.toEvent(cartDto, EventReason.DELETE.name())).willReturn(cartFactEvent);
        given(kafkaAvroSerializer.serialize(CART_TOPIC, cartFactEvent)).willReturn(serializedEvent);

        // When
        cartFactEventProducer.sendDeleteEvent(cartDto);

        // Then
        then(cartFactEventMapper).should().toEvent(cartDto, EventReason.DELETE.name());
        then(kafkaAvroSerializer).should().serialize(CART_TOPIC, cartFactEvent);

        then(outBoxRepository).should().save(OutBoxEntity.builder()
                .key(String.valueOf(cartFactEvent.getId()))
                .destination(CART_TOPIC)
                .payload(serializedEvent)
                .build());
    }
}
