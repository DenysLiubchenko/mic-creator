package org.example.boot.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.delta.DeleteCartDeltaEvent;
import org.example.delta.DiscountCartDeltaEvent;
import org.example.delta.ModifyProductItemCartDeltaEvent;
import org.example.delta.RemoveProductItemCartDeltaEvent;
import org.example.fact.CartFactEvent;
import org.example.fact.DiscountFactEvent;
import org.example.fact.ProductFactEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Configuration
public class TestKafkaConfig {
    public static final String DISCOUNT_FACT_TOPIC = "discount-fact";
    public static final String DISCOUNT_DELTA_TOPIC = "discount-delta";
    public static final String PRODUCT_FACT_TOPIC = "product-fact";
    public static final String PRODUCT_DELTA_TOPIC = "product-delta";
    public static final String CART_FACT_TOPIC = "cart-fact";
    public static final String CART_DELTA_TOPIC = "cart-delta";

    @Bean
    public ConsumerFactory<String, Object> testConsumerFactory(
            @Value("${kafka.bootstrap-servers}") final String bootstrapServers,
            @Value("${kafka.schema.registry.url}") final String schemaRegistryUrl) {
        final Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.toString());
        config.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        config.put(KafkaAvroDeserializerConfig.AUTO_REGISTER_SCHEMAS, false);
        config.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> testKafkaListenerContainerFactory(
            ConsumerFactory<String, Object> testConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(testConsumerFactory);
        return factory;
    }

    @Bean
    public KafkaTestDiscountFactEventListener discountFactEventListener() {
        return new KafkaTestDiscountFactEventListener();
    }

    public static class KafkaTestDiscountFactEventListener {
        public AtomicReference<DiscountFactEvent> deltaResult = new AtomicReference<>();
        public AtomicReference<DiscountFactEvent> factResult = new AtomicReference<>();

        @KafkaListener(groupId = "discountIT", containerFactory = "testKafkaListenerContainerFactory",
                topics = DISCOUNT_FACT_TOPIC, autoStartup = "true")
        void receiveFact(@Payload final DiscountFactEvent event) {
            log.info("Accepted fact event " + event.toString());
            factResult.set(event);
        }

        @KafkaListener(groupId = "discountIT", containerFactory = "testKafkaListenerContainerFactory",
                topics = DISCOUNT_DELTA_TOPIC, autoStartup = "true")
        void receiveDelta(@Payload final DiscountFactEvent event) {
            log.info("Accepted delta event " + event.toString());
            deltaResult.set(event);
        }
    }

    @Bean
    public KafkaTestProductFactEventListener productFactEventListener() {
        return new KafkaTestProductFactEventListener();
    }

    public static class KafkaTestProductFactEventListener {
        public AtomicReference<ProductFactEvent> deltaResult = new AtomicReference<>();
        public AtomicReference<ProductFactEvent> factResult = new AtomicReference<>();

        @KafkaListener(groupId = "productIT", containerFactory = "testKafkaListenerContainerFactory",
                topics = PRODUCT_FACT_TOPIC, autoStartup = "true")
        void receiveFact(@Payload final ProductFactEvent event) {
            log.info("Accepted fact event " + event.toString());
            factResult.set(event);
        }

        @KafkaListener(groupId = "productIT", containerFactory = "testKafkaListenerContainerFactory",
                topics = PRODUCT_DELTA_TOPIC, autoStartup = "true")
        void receiveDelta(@Payload final ProductFactEvent event) {
            log.info("Accepted delta event " + event.toString());
            deltaResult.set(event);
        }
    }

    @Bean
    public KafkaTestCartFactEventListener cartFactEventListener() {
        return new KafkaTestCartFactEventListener();
    }

    public static class KafkaTestCartFactEventListener {
        public AtomicReference<CartFactEvent> factResult = new AtomicReference<>();
        public AtomicReference<CartFactEvent> deltaCreateResult = new AtomicReference<>();
        public AtomicReference<DeleteCartDeltaEvent> deltaDeleteResult = new AtomicReference<>();
        public AtomicReference<DiscountCartDeltaEvent> deltaDiscountResult = new AtomicReference<>();
        public AtomicReference<ModifyProductItemCartDeltaEvent> deltaModifyProductResult = new AtomicReference<>();
        public AtomicReference<RemoveProductItemCartDeltaEvent> deltaRemoveProductResult = new AtomicReference<>();

        @KafkaListener(groupId = "cartIT", containerFactory = "testKafkaListenerContainerFactory",
                topics = CART_FACT_TOPIC, autoStartup = "true")
        void receiveFact(@Payload final CartFactEvent event) {
            log.info("Accepted fact event " + event.toString());
            factResult.set(event);
        }

        @KafkaListener(groupId = "cartIT", containerFactory = "testKafkaListenerContainerFactory",
                topics = CART_DELTA_TOPIC, autoStartup = "true")
        void receiveCreateDelta(@Payload final ConsumerRecord<String, ?> event) {
            log.info("Accepted delta event {}", event.value());
            switch (event.value()) {
                case CartFactEvent value -> deltaCreateResult.set(value);
                case DeleteCartDeltaEvent value -> deltaDeleteResult.set(value);
                case DiscountCartDeltaEvent value -> deltaDiscountResult.set(value);
                case ModifyProductItemCartDeltaEvent value -> deltaModifyProductResult.set(value);
                case RemoveProductItemCartDeltaEvent value -> deltaRemoveProductResult.set(value);
                default -> throw new IllegalStateException("Unexpected value: " + event.value());
            }
        }
    }
}
