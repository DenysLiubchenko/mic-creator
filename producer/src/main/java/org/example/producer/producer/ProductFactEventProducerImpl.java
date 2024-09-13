package org.example.producer.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ProductFactEvent;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.ProductDto;
import org.example.domain.producer.ProductFactEventProducer;
import org.example.producer.mapper.ProductFactEventMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductFactEventProducerImpl implements ProductFactEventProducer {
    private final String PRODUCT_TOPIC = "product-fact";
    private final ProductFactEventMapper productFactEventMapper;
    private final KafkaTemplate<String, ProductFactEvent> productKafkaTemplate;

    @Override
    public void sendCreateEvent(ProductDto productDto) {
        ProductFactEvent productFactEvent = productFactEventMapper.fromDto(productDto, EventReason.CREATE.name());
        productKafkaTemplate.send(PRODUCT_TOPIC, String.valueOf(productFactEvent.getId()),productFactEvent);
        log.info("Sent save product {} fact event to topic {}", productFactEvent, PRODUCT_TOPIC);
    }
}
