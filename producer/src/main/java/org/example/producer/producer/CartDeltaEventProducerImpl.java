package org.example.producer.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.delta.DeleteCartDeltaEvent;
import org.example.delta.DiscountCartDeltaEvent;
import org.example.delta.ModifyProductItemCartDeltaEvent;
import org.example.delta.RemoveProductItemCartDeltaEvent;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.CartDto;
import org.example.domain.dto.ProductItemDto;
import org.example.domain.producer.CartDeltaEventProducer;
import org.example.fact.CartFactEvent;
import org.example.producer.mapper.CartDeltaEventMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartDeltaEventProducerImpl implements CartDeltaEventProducer {
    private final String CART_TOPIC = "cart-delta";
    private final CartDeltaEventMapper cartDeltaEventMapper;
    private final KafkaTemplate<String, Object> cartKafkaTemplate;

    @Override
    public void sendCreateEvent(CartDto cartDto) {
        CartFactEvent cartFactEvent = cartDeltaEventMapper.toEvent(cartDto, EventReason.CREATE.name());
        cartKafkaTemplate.send(CART_TOPIC, String.valueOf(cartFactEvent.getId()), cartFactEvent);
        log.info("Sent cart create {} delta event to topic {}", cartFactEvent, CART_TOPIC);
    }

    @Override
    public void sendDeleteEvent(Long cartId) {
        DeleteCartDeltaEvent deleteCartEvent = cartDeltaEventMapper.toEvent(cartId);
        cartKafkaTemplate.send(CART_TOPIC, String.valueOf(deleteCartEvent.getId()), deleteCartEvent);
        log.info("Sent cart delete {} delta event to topic {}", deleteCartEvent, CART_TOPIC);
    }

    @Override
    public void sendAddProductItemEvent(Long cartId, ProductItemDto... productItemDtos) {
        ModifyProductItemCartDeltaEvent event = cartDeltaEventMapper.toEvent(cartId, List.of(productItemDtos), EventReason.ADD_PRODUCT_ITEM.name());
        cartKafkaTemplate.send(CART_TOPIC, String.valueOf(event.getId()), event);
        log.info("Sent cart add product {} delta event to topic {}", event, CART_TOPIC);
    }

    @Override
    public void sendUpdateProductItemEvent(Long cartId, ProductItemDto... productItemDtos) {
        ModifyProductItemCartDeltaEvent event = cartDeltaEventMapper.toEvent(cartId, List.of(productItemDtos), EventReason.CHANGE_QUANTITY_OF_PRODUCT_ITEM.name());
        cartKafkaTemplate.send(CART_TOPIC, String.valueOf(event.getId()), event);
        log.info("Sent cart update product {} delta event to topic {}", event, CART_TOPIC);
    }

    @Override
    public void sendRemoveProductItemEvent(Long cartId, Long... productIds) {
        RemoveProductItemCartDeltaEvent event = cartDeltaEventMapper.toEvent(cartId, List.of(productIds));
        cartKafkaTemplate.send(CART_TOPIC, String.valueOf(event.getId()), event);
        log.info("Sent {} event to topic {}", event, CART_TOPIC);
    }

    @Override
    public void sendAddDiscountEvent(Long cartId, String... codes) {
        DiscountCartDeltaEvent event = cartDeltaEventMapper.toDiscountEvent(cartId, List.of(codes), EventReason.ADD_DISCOUNT.name());
        cartKafkaTemplate.send(CART_TOPIC, String.valueOf(event.getId()), event);
        log.info("Sent {} event to topic {}", event, CART_TOPIC);
    }

    @Override
    public void sendRemoveDiscountEvent(Long cartId, String... codes) {
        DiscountCartDeltaEvent event = cartDeltaEventMapper.toDiscountEvent(cartId, List.of(codes), EventReason.REMOVE_DISCOUNT.name());
        cartKafkaTemplate.send(CART_TOPIC, String.valueOf(event.getId()), event);
        log.info("Sent {} event to topic {}", event, CART_TOPIC);
    }
}
