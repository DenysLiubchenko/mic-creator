package org.example.producer.producer;

import org.example.delta.DeleteCartDeltaEvent;
import org.example.delta.DiscountCartDeltaEvent;
import org.example.delta.ModifyProductItemCartDeltaEvent;
import org.example.delta.RemoveProductItemCartDeltaEvent;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.CartDto;
import org.example.domain.dto.ProductItemDto;
import org.example.fact.CartFactEvent;
import org.example.producer.ModelUtils;
import org.example.producer.mapper.CartDeltaEventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class CartDeltaEventProducerTest {

    @Mock
    private CartDeltaEventMapper cartDeltaEventMapper;

    @Mock
    private KafkaTemplate<String, Object> cartKafkaTemplate;

    @InjectMocks
    private CartDeltaEventProducerImpl cartDeltaEventProducer;

    private final String CART_TOPIC = "cart-delta";
    private final CartDto cartDto = ModelUtils.getCartDto();
    private final Long cartId = 1L;
    private final ProductItemDto productItemDto = ModelUtils.getProductItemDto();
    private final ProductItemDto[] productItemDtos = {productItemDto};
    private final String discountCode = "DISCOUNT1";
    private final String[] discountCodes = {discountCode};

    @Test
    void sendCreateEventTest() {
        // Given
        CartFactEvent cartFactEvent = ModelUtils.getCartFactEvent(EventReason.CREATE.name());
        given(cartDeltaEventMapper.toEvent(cartDto, EventReason.CREATE.name())).willReturn(cartFactEvent);

        // When
        cartDeltaEventProducer.sendCreateEvent(cartDto);

        // Then
        then(cartDeltaEventMapper).should().toEvent(cartDto, EventReason.CREATE.name());
        then(cartKafkaTemplate).should().send(CART_TOPIC, String.valueOf(cartFactEvent.getId()), cartFactEvent);
    }

    @Test
    void sendDeleteEventTest() {
        // Given
        DeleteCartDeltaEvent deleteCartEvent = ModelUtils.getDeleteCartDeltaEvent();
        given(cartDeltaEventMapper.toEvent(cartId)).willReturn(deleteCartEvent);

        // When
        cartDeltaEventProducer.sendDeleteEvent(cartId);

        // Then
        then(cartDeltaEventMapper).should().toEvent(cartId);
        then(cartKafkaTemplate).should().send(CART_TOPIC, String.valueOf(deleteCartEvent.getId()), deleteCartEvent);
    }

    @Test
    void sendAddProductItemEventTest() {
        // Given
        ModifyProductItemCartDeltaEvent event = ModelUtils.getModifyProductItemCartDeltaEvent(EventReason.ADD_PRODUCT_ITEM.name());
        given(cartDeltaEventMapper.toEvent(cartId, List.of(productItemDtos), EventReason.ADD_PRODUCT_ITEM.name())).willReturn(event);

        // When
        cartDeltaEventProducer.sendAddProductItemEvent(cartId, productItemDtos);

        // Then
        then(cartDeltaEventMapper).should().toEvent(cartId, List.of(productItemDtos), EventReason.ADD_PRODUCT_ITEM.name());
        then(cartKafkaTemplate).should().send(CART_TOPIC, String.valueOf(event.getId()), event);
    }

    @Test
    void sendUpdateProductItemEventTest() {
        // Given
        ModifyProductItemCartDeltaEvent event = ModelUtils.getModifyProductItemCartDeltaEvent(EventReason.CHANGE_QUANTITY_OF_PRODUCT_ITEM.name());
        given(cartDeltaEventMapper.toEvent(cartId, List.of(productItemDtos), EventReason.CHANGE_QUANTITY_OF_PRODUCT_ITEM.name())).willReturn(event);

        // When
        cartDeltaEventProducer.sendUpdateProductItemEvent(cartId, productItemDtos);

        // Then
        then(cartDeltaEventMapper).should().toEvent(cartId, List.of(productItemDtos), EventReason.CHANGE_QUANTITY_OF_PRODUCT_ITEM.name());
        then(cartKafkaTemplate).should().send(CART_TOPIC, String.valueOf(event.getId()), event);
    }

    @Test
    void sendRemoveProductItemEventTest() {
        // Given
        RemoveProductItemCartDeltaEvent event = ModelUtils.getRemoveProductItemCartDeltaEvent();
        given(cartDeltaEventMapper.toEvent(cartId, List.of(productItemDtos[0].getProductId()))).willReturn(event);

        // When
        cartDeltaEventProducer.sendRemoveProductItemEvent(cartId, productItemDtos[0].getProductId());

        // Then
        then(cartDeltaEventMapper).should().toEvent(cartId, List.of(productItemDtos[0].getProductId()));
        then(cartKafkaTemplate).should().send(CART_TOPIC, String.valueOf(event.getId()), event);
    }

    @Test
    void sendAddDiscountEventTest() {
        // Given
        DiscountCartDeltaEvent event = ModelUtils.getDiscountCartDeltaEvent(EventReason.CREATE.name());
        given(cartDeltaEventMapper.toDiscountEvent(cartId, List.of(discountCodes), EventReason.CREATE.name())).willReturn(event);

        // When
        cartDeltaEventProducer.sendAddDiscountEvent(cartId, discountCodes);

        // Then
        then(cartDeltaEventMapper).should().toDiscountEvent(cartId, List.of(discountCodes), EventReason.CREATE.name());
        then(cartKafkaTemplate).should().send(CART_TOPIC, String.valueOf(event.getId()), event);
    }

    @Test
    void sendRemoveDiscountEventTest() {
        // Given
        DiscountCartDeltaEvent event = ModelUtils.getDiscountCartDeltaEvent(EventReason.DELETE.name());
        given(cartDeltaEventMapper.toDiscountEvent(cartId, List.of(discountCodes), EventReason.DELETE.name())).willReturn(event);

        // When
        cartDeltaEventProducer.sendRemoveDiscountEvent(cartId, discountCodes);

        // Then
        then(cartDeltaEventMapper).should().toDiscountEvent(cartId, List.of(discountCodes), EventReason.DELETE.name());
        then(cartKafkaTemplate).should().send(CART_TOPIC, String.valueOf(event.getId()), event);
    }
}

