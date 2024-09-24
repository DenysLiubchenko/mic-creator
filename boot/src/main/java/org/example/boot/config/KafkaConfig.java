package org.example.boot.config;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class KafkaConfig {
    @Bean
    public SchemaRegistryClient schemaRegistryClient(@Value("${kafka.schema.registry.url}") String schemaRegistryUrl) {
        int maxSchemaObject = 1000;
        return new CachedSchemaRegistryClient(schemaRegistryUrl, maxSchemaObject, Collections.emptyMap());
    }

    @Bean
    public KafkaAvroSerializer kafkaAvroSerializer(SchemaRegistryClient schemaRegistryClient) {
        return new KafkaAvroSerializer(schemaRegistryClient);
    }
}
