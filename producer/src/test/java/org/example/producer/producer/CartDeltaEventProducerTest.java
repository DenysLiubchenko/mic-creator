package org.example.producer.producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.example.delta.DeleteCartDeltaEvent;
import org.example.delta.DiscountCartDeltaEvent;
import org.example.delta.ModifyProductItemCartDeltaEvent;
import org.example.delta.RemoveProductItemCartDeltaEvent;
import org.example.domain.constant.EventReason;
import org.example.domain.dto.CartDto;
import org.example.domain.dto.ProductItemDto;
import org.example.fact.CartFactEvent;
import org.example.producer.ModelUtils;
import org.example.producer.adapter.OutBoxRepository;
import org.example.producer.entity.OutBoxEntity;
import org.example.producer.mapper.CartDeltaEventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class CartDeltaEventProducerTest {

    @Mock
    private CartDeltaEventMapper cartDeltaEventMapper;

    @Mock
    private OutBoxRepository outBoxRepository;

    @Mock
    private KafkaAvroSerializer kafkaAvroSerializer;

    @InjectMocks
    private CartDeltaEventProducerImpl cartDeltaEventProducer;

    private final String CART_TOPIC = "cart-delta";
    private final CartDto cartDto = ModelUtils.getCartDto();
    private final Long cartId = 1L;
    private final ProductItemDto productItemDto = ModelUtils.getProductItemDto();
    private final ProductItemDto[] productItemDtos = {productItemDto};
    private final String discountCode = "DISCOUNT1";
    private final String[] discountCodes = {discountCode};
    private final byte[] serializedEvent = new byte[]{1, 2, 3};

    @Test
    void sendCreateEventTest() {
        // Given
        CartFactEvent cartFactEvent = ModelUtils.getCartFactEvent(EventReason.CREATE.name());

        given(cartDeltaEventMapper.toEvent(cartDto, EventReason.CREATE.name())).willReturn(cartFactEvent);
        given(kafkaAvroSerializer.serialize(CART_TOPIC + cartFactEvent.getSchema().getFullName(), cartFactEvent)).willReturn(serializedEvent);

        // When
        cartDeltaEventProducer.sendCreateEvent(cartDto);

        // Then
        then(cartDeltaEventMapper).should().toEvent(cartDto, EventReason.CREATE.name());
        then(kafkaAvroSerializer).should().serialize(CART_TOPIC + cartFactEvent.getSchema().getFullName(), cartFactEvent);

        then(outBoxRepository).should().save(OutBoxEntity.builder()
                .key(String.valueOf(cartFactEvent.getId()))
                .destination(CART_TOPIC)
                .payload(serializedEvent)
                .build());
    }

    @Test
    void sendDeleteEventTest() {
        // Given
        DeleteCartDeltaEvent deleteCartEvent = ModelUtils.getDeleteCartDeltaEvent();

        given(cartDeltaEventMapper.toEvent(cartId)).willReturn(deleteCartEvent);
        given(kafkaAvroSerializer.serialize(CART_TOPIC + deleteCartEvent.getSchema().getFullName(), deleteCartEvent)).willReturn(serializedEvent);

        // When
        cartDeltaEventProducer.sendDeleteEvent(cartId);

        // Then
        then(cartDeltaEventMapper).should().toEvent(cartId);
        then(kafkaAvroSerializer).should().serialize(CART_TOPIC + deleteCartEvent.getSchema().getFullName(), deleteCartEvent);

        then(outBoxRepository).should().save(OutBoxEntity.builder()
                .key(String.valueOf(deleteCartEvent.getId()))
                .destination(CART_TOPIC)
                .payload(serializedEvent)
                .build());
    }

    @Test
    void sendAddProductItemEventTest() {
        // Given
        ModifyProductItemCartDeltaEvent event = ModelUtils.getModifyProductItemCartDeltaEvent(EventReason.ADD_PRODUCT_ITEM.name());

        given(cartDeltaEventMapper.toEvent(cartId, List.of(productItemDtos), EventReason.ADD_PRODUCT_ITEM.name())).willReturn(event);
        given(kafkaAvroSerializer.serialize(CART_TOPIC + event.getSchema().getFullName(), event)).willReturn(serializedEvent);

        // When
        cartDeltaEventProducer.sendAddProductItemEvent(cartId, productItemDtos);

        // Then
        then(cartDeltaEventMapper).should().toEvent(cartId, List.of(productItemDtos), EventReason.ADD_PRODUCT_ITEM.name());
        then(kafkaAvroSerializer).should().serialize(CART_TOPIC + event.getSchema().getFullName(), event);

        then(outBoxRepository).should().save(OutBoxEntity.builder()
                .key(String.valueOf(event.getId()))
                .destination(CART_TOPIC)
                .payload(serializedEvent)
                .build());
    }

    @Test
    void sendUpdateProductItemEventTest() {
        // Given
        ModifyProductItemCartDeltaEvent event = ModelUtils.getModifyProductItemCartDeltaEvent(EventReason.CHANGE_QUANTITY_OF_PRODUCT_ITEM.name());

        given(cartDeltaEventMapper.toEvent(cartId, List.of(productItemDtos), EventReason.CHANGE_QUANTITY_OF_PRODUCT_ITEM.name())).willReturn(event);
        given(kafkaAvroSerializer.serialize(CART_TOPIC + event.getSchema().getFullName(), event)).willReturn(serializedEvent);

        // When
        cartDeltaEventProducer.sendUpdateProductItemEvent(cartId, productItemDtos);

        // Then
        then(cartDeltaEventMapper).should().toEvent(cartId, List.of(productItemDtos), EventReason.CHANGE_QUANTITY_OF_PRODUCT_ITEM.name());
        then(kafkaAvroSerializer).should().serialize(CART_TOPIC + event.getSchema().getFullName(), event);

        then(outBoxRepository).should().save(OutBoxEntity.builder()
                .key(String.valueOf(event.getId()))
                .destination(CART_TOPIC)
                .payload(serializedEvent)
                .build());
    }

    @Test
    void sendRemoveProductItemEventTest() {
        // Given
        RemoveProductItemCartDeltaEvent event = ModelUtils.getRemoveProductItemCartDeltaEvent();

        given(cartDeltaEventMapper.toEvent(cartId, List.of(productItemDtos[0].getProductId()))).willReturn(event);
        given(kafkaAvroSerializer.serialize(CART_TOPIC + event.getSchema().getFullName(), event)).willReturn(serializedEvent);

        // When
        cartDeltaEventProducer.sendRemoveProductItemEvent(cartId, productItemDtos[0].getProductId());

        // Then
        then(cartDeltaEventMapper).should().toEvent(cartId, List.of(productItemDtos[0].getProductId()));
        then(kafkaAvroSerializer).should().serialize(CART_TOPIC + event.getSchema().getFullName(), event);

        then(outBoxRepository).should().save(OutBoxEntity.builder()
                .key(String.valueOf(event.getId()))
                .destination(CART_TOPIC)
                .payload(serializedEvent)
                .build());
    }

    @Test
    void sendAddDiscountEventTest() {
        // Given
        DiscountCartDeltaEvent event = ModelUtils.getDiscountCartDeltaEvent(EventReason.ADD_DISCOUNT.name());

        given(cartDeltaEventMapper.toDiscountEvent(cartId, List.of(discountCodes), EventReason.ADD_DISCOUNT.name())).willReturn(event);
        given(kafkaAvroSerializer.serialize(CART_TOPIC + event.getSchema().getFullName(), event)).willReturn(serializedEvent);

        // When
        cartDeltaEventProducer.sendAddDiscountEvent(cartId, discountCodes);

        // Then
        then(cartDeltaEventMapper).should().toDiscountEvent(cartId, List.of(discountCodes), EventReason.ADD_DISCOUNT.name());
        then(kafkaAvroSerializer).should().serialize(CART_TOPIC + event.getSchema().getFullName(), event);

        then(outBoxRepository).should().save(OutBoxEntity.builder()
                .key(String.valueOf(event.getId()))
                .destination(CART_TOPIC)
                .payload(serializedEvent)
                .build());
    }

    @Test
    void sendRemoveDiscountEventTest() {
        // Given
        DiscountCartDeltaEvent event = ModelUtils.getDiscountCartDeltaEvent(EventReason.REMOVE_DISCOUNT.name());

        given(cartDeltaEventMapper.toDiscountEvent(cartId, List.of(discountCodes), EventReason.REMOVE_DISCOUNT.name())).willReturn(event);
        given(kafkaAvroSerializer.serialize(CART_TOPIC + event.getSchema().getFullName(), event)).willReturn(serializedEvent);

        // When
        cartDeltaEventProducer.sendRemoveDiscountEvent(cartId, discountCodes);

        // Then
        then(cartDeltaEventMapper).should().toDiscountEvent(cartId, List.of(discountCodes), EventReason.REMOVE_DISCOUNT.name());
        then(kafkaAvroSerializer).should().serialize(CART_TOPIC + event.getSchema().getFullName(), event);

        then(outBoxRepository).should().save(OutBoxEntity.builder()
                .key(String.valueOf(event.getId()))
                .destination(CART_TOPIC)
                .payload(serializedEvent)
                .build());
    }
}
