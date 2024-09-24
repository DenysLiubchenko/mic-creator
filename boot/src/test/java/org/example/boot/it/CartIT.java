package org.example.boot.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaString;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.apache.avro.Schema;
import org.example.api.generated.model.CartDTO;
import org.example.api.generated.model.ProductItemDTO;
import org.example.boot.BootApplication;
import org.example.dao.adapter.CartJpaAdapter;
import org.example.dao.entity.CartEntity;
import org.example.dao.entity.ProductItemEntity;
import org.example.delta.DeleteCartDeltaEvent;
import org.example.delta.DiscountCartDeltaEvent;
import org.example.delta.ModifyProductItemCartDeltaEvent;
import org.example.delta.RemoveProductItemCartDeltaEvent;
import org.example.domain.constant.EventReason;
import org.example.fact.CartFactEvent;
import org.example.fact.ProductItem;
import org.example.producer.adapter.OutBoxJpaAdapter;
import org.example.producer.entity.OutBoxEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {BootApplication.class})
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase(provider = ZONKY, refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureWireMock(port = 0)
@Sql(scripts = {"/start.sql", "/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@ActiveProfiles("test")
@Transactional
public class CartIT {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CartJpaAdapter cartJpaAdapter;
    @Autowired
    private OutBoxJpaAdapter outBoxJpaAdapter;
    @Autowired
    private KafkaAvroSerializer kafkaAvroSerializer;
    private final String CART_FACT_TOPIC = "cart-fact";
    private final String CART_DELTA_TOPIC = "cart-delta";


    @BeforeEach
    public void setUp() throws Exception {
        WireMock.reset();
        WireMock.resetAllRequests();
        WireMock.resetAllScenarios();
        WireMock.resetToDefault();

        registerSchema(1, CART_FACT_TOPIC, CartFactEvent.getClassSchema());
        registerSchema(2, CART_DELTA_TOPIC + "-" + CartFactEvent.getClassSchema().getFullName(), CartFactEvent.getClassSchema());
        registerSchema(3, CART_DELTA_TOPIC + "-" + DiscountCartDeltaEvent.getClassSchema().getFullName(), DiscountCartDeltaEvent.getClassSchema());
        registerSchema(4, CART_DELTA_TOPIC + "-" + ModifyProductItemCartDeltaEvent.getClassSchema().getFullName(), ModifyProductItemCartDeltaEvent.getClassSchema());
        registerSchema(5, CART_DELTA_TOPIC + "-" + RemoveProductItemCartDeltaEvent.getClassSchema().getFullName(), RemoveProductItemCartDeltaEvent.getClassSchema());
        registerSchema(6, CART_DELTA_TOPIC + "-" + DeleteCartDeltaEvent.getClassSchema().getFullName(), DeleteCartDeltaEvent.getClassSchema());
    }

    private void registerSchema(int schemaId, String topic, Schema schema) throws IOException {
        stubFor(post(urlPathMatching("/subjects/" + topic + "-value"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":" + schemaId + "}")));

        final SchemaString schemaString = new SchemaString(schema.toString());
        stubFor(get(urlPathMatching("/schemas/ids/" + schemaId))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody(schemaString.toJson())));
    }

    @Test
    void updateCartTest() throws Exception {
        // Given
        long cartId = 52;
        var cartDTO = new CartDTO();
        String newDiscount = "CODE_2000";
        String oldDiscount = "SUMMER21";
        cartDTO.setDiscounts(Set.of(oldDiscount, newDiscount));
        cartDTO.setProducts(Set.of(new ProductItemDTO(3L)));

        // When
        mockMvc.perform(MockMvcRequestBuilders.put("/cart/{cartId}", cartId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartDTO))).andExpect(status().isNoContent());

        // Then
        CartEntity cartEntity = cartJpaAdapter.findByIdFetchDiscounts(cartId).get();
        cartJpaAdapter.findByIdFetchProducts(cartId).get();
        assertThat(cartEntity.getDiscounts()).isEqualTo(cartDTO.getDiscounts());
        assertThat(cartEntity.getProducts().size()).isEqualTo(cartDTO.getProducts().size());
        cartEntity.getProducts().forEach(pe ->
                assertThat(cartDTO.getProducts().stream()
                        .anyMatch(p -> pe.getQuantity().equals(p.getQuantity()) &&
                                pe.getId().getProductId().equals(p.getProductId())))
                        .isTrue());

        CartFactEvent cartFactEvent = CartFactEvent.newBuilder()
                .setReason(EventReason.UPDATE.name())
                .setId(cartEntity.getId())
                .setProducts(cartEntity.getProducts().stream().map(this::fromEntity).toList())
                .setDiscounts(cartDTO.getDiscounts().stream().toList())
                .build();
        DiscountCartDeltaEvent addDiscountEvent = DiscountCartDeltaEvent.newBuilder()
                .setReason(EventReason.ADD_DISCOUNT.name())
                .setId(cartId)
                .setDiscounts(List.of(newDiscount))
                .build();
        ModifyProductItemCartDeltaEvent updateProductEvent = ModifyProductItemCartDeltaEvent.newBuilder()
                .setReason(EventReason.CHANGE_QUANTITY_OF_PRODUCT_ITEM.name())
                .setId(cartId)
                .setProducts(List.of(new org.example.delta.ProductItem(3L, 1)))
                .build();
        RemoveProductItemCartDeltaEvent removeProductEvent = RemoveProductItemCartDeltaEvent.newBuilder()
                .setId(cartId)
                .setProductIds(List.of(2L))
                .build();

        assertOutbox(1L, cartEntity.getId().toString(), CART_FACT_TOPIC, cartFactEvent);
        assertOutbox(2L, cartEntity.getId().toString(), CART_DELTA_TOPIC, CART_DELTA_TOPIC + "-" + DiscountCartDeltaEvent.getClassSchema().getFullName(), addDiscountEvent);
        assertOutbox(3L, cartEntity.getId().toString(), CART_DELTA_TOPIC, CART_DELTA_TOPIC + "-" + ModifyProductItemCartDeltaEvent.getClassSchema().getFullName(), updateProductEvent);
        assertOutbox(4L, cartEntity.getId().toString(), CART_DELTA_TOPIC, CART_DELTA_TOPIC + "-" + RemoveProductItemCartDeltaEvent.getClassSchema().getFullName(), removeProductEvent);
    }

    @Test
    void deleteCartTest() throws Exception {
        // Given
        Long cartId = 50L;
        CartEntity cartEntity = cartJpaAdapter.findByIdFetchDiscounts(cartId).get();
        cartJpaAdapter.findByIdFetchProducts(cartId).get();

        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/cart/{cartId}", cartId))
                .andExpect(status().isNoContent());

        // Then
        assertThat(cartJpaAdapter.existsById(cartId)).isFalse();

        CartFactEvent cartFactEvent = CartFactEvent.newBuilder()
                .setReason(EventReason.DELETE.name())
                .setId(cartId)
                .setProducts(cartEntity.getProducts().stream().map(this::fromEntity).toList())
                .setDiscounts(cartEntity.getDiscounts().stream().toList())
                .build();
        DeleteCartDeltaEvent cartDeltaEvent = DeleteCartDeltaEvent.newBuilder()
                .setId(cartId)
                .build();

        assertOutbox(1L, cartEntity.getId().toString(), CART_FACT_TOPIC, cartFactEvent);
        assertOutbox(2L, cartEntity.getId().toString(), CART_DELTA_TOPIC, CART_DELTA_TOPIC + "-" + DeleteCartDeltaEvent.getClassSchema().getFullName(), cartDeltaEvent);
    }

    @Test
    void addDiscountToCartTest() throws Exception {
        // Given
        long cartId = 51L;
        String newDiscount = "SUMMER21";

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/cart/{cartId}/discount", cartId)
                        .param("code", newDiscount))
                .andExpect(status().isNoContent());

        // Then
        CartEntity cartEntity = cartJpaAdapter.findByIdFetchDiscounts(cartId).get();
        cartJpaAdapter.findByIdFetchProducts(cartId).get();
        assertThat(cartEntity.getDiscounts().contains(newDiscount)).isTrue();

        CartFactEvent cartFactEvent = CartFactEvent.newBuilder()
                .setReason(EventReason.UPDATE.name())
                .setId(cartId)
                .setProducts(cartEntity.getProducts().stream().map(this::fromEntity).toList())
                .setDiscounts(cartEntity.getDiscounts().stream().toList())
                .build();
        DiscountCartDeltaEvent cartDeltaEvent = DiscountCartDeltaEvent.newBuilder()
                .setReason(EventReason.ADD_DISCOUNT.name())
                .setId(cartId)
                .setDiscounts(List.of(newDiscount))
                .build();
        assertOutbox(1L, cartEntity.getId().toString(), CART_FACT_TOPIC, cartFactEvent);
        assertOutbox(2L, cartEntity.getId().toString(), CART_DELTA_TOPIC, CART_DELTA_TOPIC + "-" + DiscountCartDeltaEvent.getClassSchema().getFullName(), cartDeltaEvent);
    }

    @Test
    void removeDiscountFromCartTest() throws Exception {
        // Given
        long cartId = 51L;
        String oldDiscount = "SUMMER21";

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/cart/{cartId}/discount/remove", cartId)
                        .param("code", oldDiscount))
                .andExpect(status().isNoContent());

        // Then
        CartEntity cartEntity = cartJpaAdapter.findByIdFetchDiscounts(cartId).get();
        cartJpaAdapter.findByIdFetchProducts(cartId).get();
        assertThat(cartEntity.getDiscounts().contains(oldDiscount)).isFalse();

        CartFactEvent cartFactEvent = CartFactEvent.newBuilder()
                .setReason(EventReason.UPDATE.name())
                .setId(cartId)
                .setProducts(cartEntity.getProducts().stream().map(this::fromEntity).toList())
                .setDiscounts(cartEntity.getDiscounts().stream().toList())
                .build();
        DiscountCartDeltaEvent cartDeltaEvent = DiscountCartDeltaEvent.newBuilder()
                .setReason(EventReason.REMOVE_DISCOUNT.name())
                .setId(cartId)
                .setDiscounts(List.of(oldDiscount))
                .build();

        assertOutbox(1L, cartEntity.getId().toString(), CART_FACT_TOPIC, cartFactEvent);
        assertOutbox(2L, cartEntity.getId().toString(), CART_DELTA_TOPIC, CART_DELTA_TOPIC + "-" + DiscountCartDeltaEvent.getClassSchema().getFullName(), cartDeltaEvent);
    }

    @Test
    void addProductToCartTest() throws Exception {
        // Given
        long cartId = 51L;
        ProductItemDTO productItemDTO = new ProductItemDTO(3L);

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/cart/{cartId}/product", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productItemDTO)))
                .andExpect(status().isNoContent());

        // Then
        CartEntity cartEntity = cartJpaAdapter.findByIdFetchDiscounts(cartId).get();
        cartJpaAdapter.findByIdFetchProducts(cartId).get();
        assertThat(cartEntity.getProducts().stream().anyMatch(p ->
                p.getId().getProductId().equals(productItemDTO.getProductId()) &&
                        p.getQuantity().equals(productItemDTO.getQuantity()))).isTrue();

        var newProductItem = new org.example.delta.ProductItem(3L, 1);

        CartFactEvent cartFactEvent = CartFactEvent.newBuilder()
                .setReason(EventReason.UPDATE.name())
                .setId(cartId)
                .setProducts(cartEntity.getProducts().stream().map(this::fromEntity).toList())
                .setDiscounts(cartEntity.getDiscounts().stream().toList())
                .build();
        ModifyProductItemCartDeltaEvent cartDeltaEvent = ModifyProductItemCartDeltaEvent.newBuilder()
                .setReason(EventReason.ADD_PRODUCT_ITEM.name())
                .setId(cartId)
                .setProducts(List.of(newProductItem))
                .build();

        assertOutbox(1L, cartEntity.getId().toString(), CART_FACT_TOPIC, cartFactEvent);
        assertOutbox(2L, cartEntity.getId().toString(), CART_DELTA_TOPIC, CART_DELTA_TOPIC + "-" + ModifyProductItemCartDeltaEvent.getClassSchema().getFullName(), cartDeltaEvent);
    }

    @Test
    void removeProductFromCartTest() throws Exception {
        // Given
        long cartId = 51L;
        long productId = 2L;

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/cart/{cartId}/product/remove", cartId)
                        .param("productId", String.valueOf(productId)))
                .andExpect(status().isNoContent());

        // Then
        CartEntity cartEntity = cartJpaAdapter.findByIdFetchDiscounts(cartId).get();
        cartJpaAdapter.findByIdFetchProducts(cartId).get();
        assertThat(cartEntity.getProducts().stream().anyMatch(p ->
                p.getId().getProductId().equals(productId))).isFalse();

        CartFactEvent cartFactEvent = CartFactEvent.newBuilder()
                .setReason(EventReason.UPDATE.name())
                .setId(cartId)
                .setProducts(cartEntity.getProducts().stream().map(this::fromEntity).toList())
                .setDiscounts(cartEntity.getDiscounts().stream().toList())
                .build();
        RemoveProductItemCartDeltaEvent cartDeltaEvent = RemoveProductItemCartDeltaEvent.newBuilder()
                .setId(cartId)
                .setProductIds(List.of(productId))
                .build();

        assertOutbox(1L, cartEntity.getId().toString(), CART_FACT_TOPIC, cartFactEvent);
        assertOutbox(2L, cartEntity.getId().toString(), CART_DELTA_TOPIC, CART_DELTA_TOPIC + "-" + RemoveProductItemCartDeltaEvent.getClassSchema().getFullName(), cartDeltaEvent);
    }

    private void assertOutbox(Long id, String key, String topic, Object event) {
        Optional<OutBoxEntity> outboxEntity = outBoxJpaAdapter.findById(id);
        assertThat(outboxEntity.isPresent()).isTrue();
        assertThat(outboxEntity.get().getKey()).isEqualTo(key);
        assertThat(outboxEntity.get().getDestination()).isEqualTo(topic);
        assertThat(outboxEntity.get().getPayload()).isEqualTo(kafkaAvroSerializer.serialize(topic, event));
    }

    private void assertOutbox(Long id, String key, String topic, String subject, Object event) {
        Optional<OutBoxEntity> outboxEntity = outBoxJpaAdapter.findById(id);
        assertThat(outboxEntity.isPresent()).isTrue();
        assertThat(outboxEntity.get().getKey()).isEqualTo(key);
        assertThat(outboxEntity.get().getDestination()).isEqualTo(topic);
        assertThat(outboxEntity.get().getPayload()).isEqualTo(kafkaAvroSerializer.serialize(subject, event));
    }

    private ProductItem fromEntity(ProductItemEntity productItemEntity) {
        return ProductItem.newBuilder()
                .setProductId(productItemEntity.getId().getProductId())
                .setQuantity(productItemEntity.getQuantity())
                .build();
    }
}
