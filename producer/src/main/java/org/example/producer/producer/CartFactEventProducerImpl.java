package org.example.producer.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.CartFactEvent;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.CartDto;
import org.example.domain.producer.CartFactEventProducer;
import org.example.producer.mapper.CartFactEventMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartFactEventProducerImpl implements CartFactEventProducer {
    private final String CART_TOPIC = "cart-fact";
    private final CartFactEventMapper cartFactEventMapper;
    private final KafkaTemplate<String, CartFactEvent> cartKafkaTemplate;

    @Override
    public void sendCreateEvent(CartDto cartDto) {
        CartFactEvent cartFactEvent = cartFactEventMapper.fromDto(cartDto, EventReason.CREATE.name());
        cartKafkaTemplate.send(CART_TOPIC, String.valueOf(cartFactEvent.getId()), cartFactEvent);
        log.info("Sent save cart {} fact event to topic {}", cartFactEvent, CART_TOPIC);
    }

    @Override
    public void sendUpdateEvent(CartDto cartDto) {
        CartFactEvent cartFactEvent = cartFactEventMapper.fromDto(cartDto, EventReason.UPDATE.name());
        cartKafkaTemplate.send(CART_TOPIC, String.valueOf(cartFactEvent.getId()), cartFactEvent);
        log.info("Sent update cart {} fact event to topic {}", cartFactEvent, CART_TOPIC);
    }

    @Override
    public void sendDeleteEvent(CartDto cartDto) {
        CartFactEvent cartFactEvent = cartFactEventMapper.fromDto(cartDto, EventReason.DELETE.name());
        cartKafkaTemplate.send(CART_TOPIC, String.valueOf(cartFactEvent.getId()), cartFactEvent);
        log.info("Sent delete cart {} fact event to topic {}", cartFactEvent, CART_TOPIC);
    }
}
