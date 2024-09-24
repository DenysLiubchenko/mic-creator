package org.example.producer.producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.ProductDto;
import org.example.fact.ProductFactEvent;
import org.example.producer.ModelUtils;
import org.example.producer.adapter.OutBoxJpaAdapter;
import org.example.producer.entity.OutBoxEntity;
import org.example.producer.mapper.ProductFactEventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class ProductDeltaEventProducerTest {

    @Mock
    private ProductFactEventMapper productFactEventMapper;

    @Mock
    private OutBoxJpaAdapter outBoxJpaAdapter;

    @Mock
    private KafkaAvroSerializer kafkaAvroSerializer;

    @InjectMocks
    private ProductDeltaEventProducerImpl productDeltaEventProducer;

    private final String PRODUCT_TOPIC = "product-delta";
    private final ProductDto productDto = ModelUtils.getProductDto();
    private final byte[] serializedEvent = new byte[]{1, 2, 3};

    @Test
    void sendCreateEventTest() {
        // Given
        ProductFactEvent productFactEvent = ModelUtils.getProductFactEvent(EventReason.CREATE.name());
        given(productFactEventMapper.toEvent(productDto, EventReason.CREATE.name())).willReturn(productFactEvent);
        given(kafkaAvroSerializer.serialize(PRODUCT_TOPIC + productFactEvent.getSchema().getFullName(), productFactEvent))
                .willReturn(serializedEvent);

        // When
        productDeltaEventProducer.sendCreateEvent(productDto);

        // Then
        then(productFactEventMapper).should().toEvent(productDto, EventReason.CREATE.name());
        then(kafkaAvroSerializer).should().serialize(PRODUCT_TOPIC + productFactEvent.getSchema().getFullName(), productFactEvent);
        then(outBoxJpaAdapter).should().save(OutBoxEntity.builder()
                .key(String.valueOf(productFactEvent.getId()))
                .destination(PRODUCT_TOPIC)
                .payload(serializedEvent)
                .build());
    }
}

