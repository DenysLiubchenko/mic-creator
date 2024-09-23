package org.example.producer.producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.DiscountDto;
import org.example.domain.producer.DiscountFactEventProducer;
import org.example.fact.DiscountFactEvent;
import org.example.producer.adapter.OutBoxRepository;
import org.example.producer.entity.OutBoxEntity;
import org.example.producer.mapper.DiscountFactEventMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountFactEventProducerImpl implements DiscountFactEventProducer {
    private final String DISCOUNT_TOPIC = "discount-fact";
    private final DiscountFactEventMapper discountFactEventMapper;
    private final OutBoxRepository outBoxRepository;
    private final KafkaAvroSerializer kafkaAvroSerializer;

    @Override
    public void sendCreateEvent(DiscountDto discountDto) {
        DiscountFactEvent event = discountFactEventMapper.toEvent(discountDto, EventReason.CREATE.name());
        byte[] payload = kafkaAvroSerializer.serialize(DISCOUNT_TOPIC, event);

        outBoxRepository.save(OutBoxEntity.builder()
                .key(String.valueOf(event.getCode()))
                .destination(DISCOUNT_TOPIC)
                .payload(payload)
                .build());
        log.info("Sent save discount \"{}\" fact event to topic {}", event, DISCOUNT_TOPIC);
    }
}
