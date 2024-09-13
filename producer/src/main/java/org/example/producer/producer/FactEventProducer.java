package org.example.producer.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.CartFactEvent;
import org.example.DiscountFactEvent;
import org.example.ProductFactEvent;
import org.example.producer.mapper.CartFactEventMapper;
import org.example.producer.mapper.DiscountFactEventMapper;
import org.example.producer.mapper.ProductFactEventMapper;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.CartDto;
import org.example.domain.dto.DiscountDto;
import org.example.domain.dto.ProductDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FactEventProducer {
    private final String DISCOUNT_TOPIC = "discount-fact";
    private final String PRODUCT_TOPIC = "product-fact";
    private final String CART_TOPIC = "cart-fact";
    private final DiscountFactEventMapper discountFactEventMapper;
    private final ProductFactEventMapper productFactEventMapper;
    private final CartFactEventMapper cartFactEventMapper;
    private final KafkaTemplate<String, DiscountFactEvent> discountKafkaTemplate;
    private final KafkaTemplate<String, ProductFactEvent> productKafkaTemplate;
    private final KafkaTemplate<String, CartFactEvent> cartKafkaTemplate;

    public void sendCreateEvent(DiscountDto discountDto) {
        DiscountFactEvent discountFactEvent = discountFactEventMapper.fromDto(discountDto, EventReason.CREATE.name());
        discountKafkaTemplate.send(DISCOUNT_TOPIC, discountFactEvent.getCode(),  discountFactEvent);
        log.info("Sent save discount \"{}\" fact event to topic {}", discountFactEvent, DISCOUNT_TOPIC);
    }

    public void sendCreateEvent(ProductDto productDto) {
        ProductFactEvent productFactEvent = productFactEventMapper.fromDto(productDto, EventReason.CREATE.name());
        productKafkaTemplate.send(PRODUCT_TOPIC, String.valueOf(productFactEvent.getId()),productFactEvent);
        log.info("Sent save product {} fact event to topic {}", productFactEvent, PRODUCT_TOPIC);
    }

    public void sendCreateEvent(CartDto cartDto) {
        CartFactEvent cartFactEvent = cartFactEventMapper.fromDto(cartDto, EventReason.CREATE.name());
        cartKafkaTemplate.send(CART_TOPIC, String.valueOf(cartFactEvent.getId()), cartFactEvent);
        log.info("Sent save cart {} fact event to topic {}", cartFactEvent, CART_TOPIC);
    }

    public void sendUpdateEvent(Long cartId, CartDto cartDto) {
        cartDto.setId(cartId);
        CartFactEvent cartFactEvent = cartFactEventMapper.fromDto(cartDto, EventReason.UPDATE.name());
        cartKafkaTemplate.send(CART_TOPIC, String.valueOf(cartFactEvent.getId()), cartFactEvent);
        log.info("Sent update cart {} fact event to topic {}", cartFactEvent, CART_TOPIC);
    }

    public void sendDeleteEvent(Long cartId, CartDto cartDto) {
        cartDto.setId(cartId);
        CartFactEvent cartFactEvent = cartFactEventMapper.fromDto(cartDto, EventReason.UPDATE.name());
        cartKafkaTemplate.send(CART_TOPIC, String.valueOf(cartFactEvent.getId()), cartFactEvent);
        log.info("Sent delete cart {} fact event to topic {}", cartFactEvent, CART_TOPIC);
    }
}
