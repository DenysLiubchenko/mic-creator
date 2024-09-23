package org.example.producer.producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.DiscountDto;
import org.example.fact.DiscountFactEvent;
import org.example.producer.ModelUtils;
import org.example.producer.adapter.OutBoxRepository;
import org.example.producer.entity.OutBoxEntity;
import org.example.producer.mapper.DiscountFactEventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class DiscountDeltaEventProducerTest {

    @Mock
    private DiscountFactEventMapper discountFactEventMapper;

    @Mock
    private OutBoxRepository outBoxRepository;

    @Mock
    private KafkaAvroSerializer kafkaAvroSerializer;

    @InjectMocks
    private DiscountDeltaEventProducerImpl discountDeltaEventProducer;

    private final String DISCOUNT_TOPIC = "discount-delta";
    private final DiscountDto discountDto = ModelUtils.getDiscountDto();
    private final byte[] serializedEvent = new byte[]{1, 2, 3}; // Moved to class level

    @Test
    void sendCreateEventTest() {
        // Given
        DiscountFactEvent discountFactEvent = ModelUtils.getDiscountFactEvent(EventReason.CREATE.name());
        given(discountFactEventMapper.toEvent(discountDto, EventReason.CREATE.name())).willReturn(discountFactEvent);
        given(kafkaAvroSerializer.serialize(DISCOUNT_TOPIC + discountFactEvent.getSchema().getFullName(), discountFactEvent)).willReturn(serializedEvent);

        // When
        discountDeltaEventProducer.sendCreateEvent(discountDto);

        // Then
        then(discountFactEventMapper).should().toEvent(discountDto, EventReason.CREATE.name());
        then(kafkaAvroSerializer).should().serialize(DISCOUNT_TOPIC + discountFactEvent.getSchema().getFullName(), discountFactEvent);

        then(outBoxRepository).should().save(OutBoxEntity.builder()
                .key(String.valueOf(discountFactEvent.getCode()))
                .destination(DISCOUNT_TOPIC)
                .payload(serializedEvent)
                .build());
    }
}

