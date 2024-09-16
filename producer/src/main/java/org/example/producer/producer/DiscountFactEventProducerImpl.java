package org.example.producer.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fact.DiscountFactEvent;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.DiscountDto;
import org.example.domain.producer.DiscountFactEventProducer;
import org.example.producer.mapper.DiscountFactEventMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountFactEventProducerImpl implements DiscountFactEventProducer {
    private final String DISCOUNT_TOPIC = "discount-fact";
    private final DiscountFactEventMapper discountFactEventMapper;
    private final KafkaTemplate<String, Object> discountKafkaTemplate;

    @Override
    public void sendCreateEvent(DiscountDto discountDto) {
        DiscountFactEvent discountFactEvent = discountFactEventMapper.toEvent(discountDto, EventReason.CREATE.name());
        discountKafkaTemplate.send(DISCOUNT_TOPIC, discountFactEvent.getCode(),  discountFactEvent);
        log.info("Sent save discount \"{}\" fact event to topic {}", discountFactEvent, DISCOUNT_TOPIC);
    }
}
