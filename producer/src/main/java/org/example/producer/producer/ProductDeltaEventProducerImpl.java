package org.example.producer.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fact.ProductFactEvent;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.ProductDto;
import org.example.domain.producer.ProductDeltaEventProducer;
import org.example.producer.mapper.ProductFactEventMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDeltaEventProducerImpl implements ProductDeltaEventProducer {
    private final String PRODUCT_TOPIC = "product-delta";
    private final ProductFactEventMapper productFactEventMapper;
    private final KafkaTemplate<String, Object> productKafkaTemplate;

    @Override
    public void sendCreateEvent(ProductDto productDto) {
        ProductFactEvent productFactEvent = productFactEventMapper.toEvent(productDto, EventReason.CREATE.name());
        productKafkaTemplate.send(PRODUCT_TOPIC, String.valueOf(productFactEvent.getId()),productFactEvent);
        log.info("Sent save product {} event to topic {}", productFactEvent, PRODUCT_TOPIC);
    }
}
