package org.example.boot.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaString;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.apache.avro.Schema;
import org.example.boot.BootApplication;
import org.example.boot.ModelUtils;
import org.example.dao.adapter.ProductJpaAdapter;
import org.example.dao.entity.ProductEntity;
import org.example.domain.constant.EventReason;
import org.example.fact.ProductFactEvent;
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

import java.io.IOException;
import java.util.Optional;

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
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureWireMock(port = 0)
@Sql(scripts = "/start.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@ActiveProfiles("test")
public class ProductIT {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductJpaAdapter productJpaAdapter;
    @Autowired
    private OutBoxJpaAdapter outBoxJpaAdapter;
    @Autowired
    private KafkaAvroSerializer kafkaAvroSerializer;
    private final String PRODUCT_FACT_TOPIC = "product-fact";
    private final String PRODUCT_DELTA_TOPIC = "product-delta";
    private final String PRODUCT_DELTA_SUBJECT = PRODUCT_DELTA_TOPIC + "-" + ProductFactEvent.getClassSchema().getFullName();

    @BeforeEach
    public void setUp() throws Exception {
        WireMock.reset();
        WireMock.resetAllRequests();
        WireMock.resetAllScenarios();
        WireMock.resetToDefault();

        registerSchema(1, PRODUCT_FACT_TOPIC, ProductFactEvent.getClassSchema());
        registerSchema(2, PRODUCT_DELTA_SUBJECT, ProductFactEvent.getClassSchema());
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
    void saveProductTest() throws Exception {
        // Given
        var productDTO = ModelUtils.getProductDTO();

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO))).andExpect(status().isCreated());

        // Then
        Optional<ProductEntity> optionalProduct = productJpaAdapter.findById(1L);
        assertThat(optionalProduct.isPresent()).isTrue();
        ProductEntity productEntity = optionalProduct.get();
        assertThat(productEntity.getName()).isEqualTo(productDTO.getName());
        assertThat(productEntity.getCost().doubleValue()).isEqualTo(productDTO.getCost().doubleValue());

        ProductFactEvent productFactEvent = ProductFactEvent.newBuilder()
                .setReason(EventReason.CREATE.name())
                .setId(productEntity.getId())
                .setCost(productEntity.getCost().toString())
                .setName(productEntity.getName())
                .build();

        Optional<OutBoxEntity> optionalFactOutBox = outBoxJpaAdapter.findById(1L);
        assertThat(optionalFactOutBox.isPresent()).isTrue();
        OutBoxEntity factOutBox = optionalFactOutBox.get();
        assertThat(factOutBox.getKey()).isEqualTo(productEntity.getId().toString());
        assertThat(factOutBox.getDestination()).isEqualTo(PRODUCT_FACT_TOPIC);
        assertThat(factOutBox.getPayload()).isEqualTo(kafkaAvroSerializer.serialize(PRODUCT_FACT_TOPIC, productFactEvent));

        Optional<OutBoxEntity> optionalDeltaOutBox = outBoxJpaAdapter.findById(2L);
        assertThat(optionalDeltaOutBox.isPresent()).isTrue();
        OutBoxEntity deltaOutBox = optionalDeltaOutBox.get();
        assertThat(deltaOutBox.getKey()).isEqualTo(productEntity.getId().toString());
        assertThat(deltaOutBox.getDestination()).isEqualTo(PRODUCT_DELTA_TOPIC);
        assertThat(deltaOutBox.getPayload()).isEqualTo(kafkaAvroSerializer.serialize(PRODUCT_DELTA_SUBJECT, productFactEvent));
    }
}
