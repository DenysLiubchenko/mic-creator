package org.example.producer.producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.ProductDto;
import org.example.domain.producer.ProductFactEventProducer;
import org.example.fact.ProductFactEvent;
import org.example.producer.adapter.OutBoxJpaAdapter;
import org.example.producer.entity.OutBoxEntity;
import org.example.producer.mapper.ProductFactEventMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductFactEventProducerImpl implements ProductFactEventProducer {
    private final String PRODUCT_TOPIC = "product-fact";
    private final ProductFactEventMapper productFactEventMapper;
    private final OutBoxJpaAdapter outBoxJpaAdapter;
    private final KafkaAvroSerializer kafkaAvroSerializer;

    @Override
    public void sendCreateEvent(ProductDto productDto) {
        ProductFactEvent event = productFactEventMapper.toEvent(productDto, EventReason.CREATE.name());
        byte[] payload = kafkaAvroSerializer.serialize(PRODUCT_TOPIC, event);

        outBoxJpaAdapter.save(OutBoxEntity.builder()
                .key(String.valueOf(event.getId()))
                .destination(PRODUCT_TOPIC)
                .payload(payload)
                .build());
        log.info("Sent save product {} fact event to topic {}", event, PRODUCT_TOPIC);
    }
}
