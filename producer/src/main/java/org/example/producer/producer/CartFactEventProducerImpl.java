package org.example.producer.producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.CartDto;
import org.example.domain.producer.CartFactEventProducer;
import org.example.fact.CartFactEvent;
import org.example.producer.adapter.OutBoxRepository;
import org.example.producer.entity.OutBoxEntity;
import org.example.producer.mapper.CartFactEventMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartFactEventProducerImpl implements CartFactEventProducer {
    private final String CART_TOPIC = "cart-fact";
    private final CartFactEventMapper cartFactEventMapper;
    private final OutBoxRepository outBoxRepository;
    private final KafkaAvroSerializer kafkaAvroSerializer;

    @Override
    public void sendCreateEvent(CartDto cartDto) {
        CartFactEvent cartFactEvent = cartFactEventMapper.toEvent(cartDto, EventReason.CREATE.name());
        byte[] payload = kafkaAvroSerializer.serialize(CART_TOPIC, cartFactEvent);

        outBoxRepository.save(OutBoxEntity.builder()
                .key(String.valueOf(cartFactEvent.getId()))
                        .destination(CART_TOPIC)
                .payload(payload)
                .build());
        log.info("Sent save cart {} fact event to outbox", cartFactEvent);
    }

    @Override
    public void sendUpdateEvent(CartDto cartDto) {
        CartFactEvent cartFactEvent = cartFactEventMapper.toEvent(cartDto, EventReason.UPDATE.name());
        byte[] payload = kafkaAvroSerializer.serialize(CART_TOPIC, cartFactEvent);

        outBoxRepository.save(OutBoxEntity.builder()
                .key(String.valueOf(cartFactEvent.getId()))
                .destination(CART_TOPIC)
                .payload(payload)
                .build());
        log.info("Sent update cart {} fact event to outbox", cartFactEvent);
    }

    @Override
    public void sendDeleteEvent(CartDto cartDto) {
        CartFactEvent cartFactEvent = cartFactEventMapper.toEvent(cartDto, EventReason.DELETE.name());
        byte[] payload = kafkaAvroSerializer.serialize(CART_TOPIC, cartFactEvent);

        outBoxRepository.save(OutBoxEntity.builder()
                .key(String.valueOf(cartFactEvent.getId()))
                .destination(CART_TOPIC)
                .payload(payload)
                .build());
        log.info("Sent delete cart {} fact event to outbox", cartFactEvent);
    }
}
