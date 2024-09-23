package org.example.producer.producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.ProductDto;
import org.example.domain.producer.ProductDeltaEventProducer;
import org.example.fact.ProductFactEvent;
import org.example.producer.adapter.OutBoxRepository;
import org.example.producer.entity.OutBoxEntity;
import org.example.producer.mapper.ProductFactEventMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDeltaEventProducerImpl implements ProductDeltaEventProducer {
    private final String PRODUCT_TOPIC = "product-delta";
    private final ProductFactEventMapper productFactEventMapper;
    private final OutBoxRepository outBoxRepository;
    private final KafkaAvroSerializer kafkaAvroSerializer;

    @Override
    public void sendCreateEvent(ProductDto productDto) {
        ProductFactEvent event = productFactEventMapper.toEvent(productDto, EventReason.CREATE.name());
        byte[] payload = kafkaAvroSerializer.serialize(PRODUCT_TOPIC+event.getSchema().getFullName(), event);

        outBoxRepository.save(OutBoxEntity.builder()
                .key(String.valueOf(event.getId()))
                .destination(PRODUCT_TOPIC)
                .payload(payload)
                .build());
        log.info("Sent save product {} event to topic {}", event, PRODUCT_TOPIC);
    }
}
