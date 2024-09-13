package org.example.boot.conig;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.CartFactEvent;
import org.example.DiscountFactEvent;
import org.example.ProductFactEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    private @Value("${kafka.schema.registry.url}") String schemaRegistry;
    private @Value("${kafka.bootstrap-servers}") String bootstrapServer;

    @Bean
    public ProducerFactory<String, CartFactEvent> cartFactEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerAvroConfig(schemaRegistry, bootstrapServer));
    }

    @Bean
    public KafkaTemplate<String, CartFactEvent> cartFactEventKafkaTemplate(ProducerFactory<String, CartFactEvent> cartFactEventProducerFactory) {
        return new KafkaTemplate<>(cartFactEventProducerFactory);
    }

    @Bean
    public ProducerFactory<String, DiscountFactEvent> discountFactEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerAvroConfig(schemaRegistry, bootstrapServer));
    }

    @Bean
    public KafkaTemplate<String, DiscountFactEvent> discountFactEventKafkaTemplate(ProducerFactory<String, DiscountFactEvent> discountFactEventProducerFactory) {
        return new KafkaTemplate<>(discountFactEventProducerFactory);
    }

    @Bean
    public ProducerFactory<String, ProductFactEvent> productFactEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerAvroConfig(schemaRegistry, bootstrapServer));
    }

    @Bean
    public KafkaTemplate<String, ProductFactEvent> productFactEventKafkaTemplate(ProducerFactory<String, ProductFactEvent> productFactEventProducerFactory) {
        return new KafkaTemplate<>(productFactEventProducerFactory);
    }

    private Map<String, Object> producerAvroConfig(String schemaRegistry, String bootstrapServer) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        config.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 32 * 1024);
        config.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
        config.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, true);
        return config;
    }
}
