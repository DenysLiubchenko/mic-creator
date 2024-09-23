package org.example.boot.config;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.example.delta.DeleteCartDeltaEvent;
import org.example.delta.DeleteDiscountDeltaEvent;
import org.example.delta.DiscountCartDeltaEvent;
import org.example.delta.ModifyProductItemCartDeltaEvent;
import org.example.delta.RemoveProductItemCartDeltaEvent;
import org.example.fact.CartFactEvent;
import org.example.fact.DiscountFactEvent;
import org.example.fact.ProductFactEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Collections;

@Configuration
public class KafkaConfig {
    @Bean
    public SchemaRegistryClient schemaRegistryClient(@Value("${kafka.schema.registry.url}") String schemaRegistryUrl) {
        int maxSchemaObject = 1000;
        return new CachedSchemaRegistryClient(schemaRegistryUrl, maxSchemaObject, Collections.emptyMap());
    }

    @Bean
    public KafkaAvroSerializer kafkaAvroSerializer(SchemaRegistryClient schemaRegistryClient) throws RestClientException, IOException {
        schemaRegistryClient.register("cart-fact-value", CartFactEvent.getClassSchema());
        schemaRegistryClient.register("discount-fact-value", DiscountFactEvent.getClassSchema());
        schemaRegistryClient.register("product-fact-value", ProductFactEvent.getClassSchema());
        schemaRegistryClient.register("cart-delta"+CartFactEvent.getClassSchema().getFullName()+"-value", CartFactEvent.getClassSchema());
        schemaRegistryClient.register("discount-delta"+DiscountFactEvent.getClassSchema().getFullName()+"-value", DiscountFactEvent.getClassSchema());
        schemaRegistryClient.register("product-delta"+ProductFactEvent.getClassSchema().getFullName()+"-value", ProductFactEvent.getClassSchema());
        schemaRegistryClient.register("product-item-fact-value", org.example.fact.ProductItem.getClassSchema());
        schemaRegistryClient.register("cart-delta"+DeleteCartDeltaEvent.getClassSchema().getFullName()+"-value", DeleteCartDeltaEvent.getClassSchema());
        schemaRegistryClient.register("cart-delta"+DeleteDiscountDeltaEvent.getClassSchema().getFullName()+"-value", DeleteDiscountDeltaEvent.getClassSchema());
        schemaRegistryClient.register("cart-delta"+DiscountCartDeltaEvent.getClassSchema().getFullName()+"-value", DiscountCartDeltaEvent.getClassSchema());
        schemaRegistryClient.register("cart-delta"+ModifyProductItemCartDeltaEvent.getClassSchema().getFullName()+"-value", ModifyProductItemCartDeltaEvent.getClassSchema());
        schemaRegistryClient.register("cart-delta"+RemoveProductItemCartDeltaEvent.getClassSchema().getFullName()+"-value", RemoveProductItemCartDeltaEvent.getClassSchema());
        schemaRegistryClient.register("product-item-delta-value", org.example.delta.ProductItem.getClassSchema());
        return new KafkaAvroSerializer(schemaRegistryClient);
    }
}
