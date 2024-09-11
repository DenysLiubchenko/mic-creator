package org.example.producer.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.Cart;
import org.example.Product;
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
    public ProducerFactory<String, Cart> cartProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerAvroConfig(schemaRegistry, bootstrapServer));
    }

    @Bean
    public KafkaTemplate<String, Cart> cartKafkaTemplate(ProducerFactory<String, Cart> cartProducerFactory) {
        return new KafkaTemplate<>(cartProducerFactory);
    }

    @Bean
    public ProducerFactory<String, String> discountProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerAvroConfig(schemaRegistry, bootstrapServer));
    }

    @Bean
    public KafkaTemplate<String, String> discountKafkaTemplate(ProducerFactory<String, String> discountProducerFactory) {
        return new KafkaTemplate<>(discountProducerFactory);
    }

    @Bean
    public ProducerFactory<String, Product> productProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerStringConfig(bootstrapServer));
    }

    @Bean
    public KafkaTemplate<String, Product> productKafkaTemplate(ProducerFactory<String, Product> productProducerFactory) {
        return new KafkaTemplate<>(productProducerFactory);
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
        config.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, false);
        return config;
    }

    private Map<String, Object> producerStringConfig(String bootstrapServer) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        config.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 32 * 1024);
        return config;
    }
}
