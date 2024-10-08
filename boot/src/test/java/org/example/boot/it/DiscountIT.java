package org.example.boot.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaString;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.apache.avro.Schema;
import org.awaitility.Awaitility;
import org.example.boot.BootApplication;
import org.example.boot.ModelUtils;
import org.example.boot.config.KafkaConfig;
import org.example.boot.config.TestKafkaConfig;
import org.example.dao.adapters.DiscountJpaAdapter;
import org.example.dao.entity.DiscountEntity;
import org.example.domain.constant.EventReason;
import org.example.fact.DiscountFactEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.example.boot.config.TestKafkaConfig.DISCOUNT_DELTA_TOPIC;
import static org.example.boot.config.TestKafkaConfig.DISCOUNT_FACT_TOPIC;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {KafkaConfig.class, BootApplication.class})
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureWireMock(port = 0)
@Sql(scripts = "/start.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(controlledShutdown = true, topics = {DISCOUNT_FACT_TOPIC, DISCOUNT_DELTA_TOPIC})
public class DiscountIT {
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private KafkaListenerEndpointRegistry registry;
    @Autowired
    private TestKafkaConfig.KafkaTestDiscountFactEventListener testReceiver;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DiscountJpaAdapter discountJpaAdapter;

    @BeforeEach
    public void setUp() throws Exception {
        registry.getListenerContainers().forEach(container -> ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic()));
        WireMock.reset();
        WireMock.resetAllRequests();
        WireMock.resetAllScenarios();
        WireMock.resetToDefault();

        registerSchema(1, DISCOUNT_FACT_TOPIC, DiscountFactEvent.getClassSchema());
        registerSchema(2, DISCOUNT_DELTA_TOPIC, DiscountFactEvent.getClassSchema());
    }

    private void registerSchema(int schemaId, String topic, Schema schema) throws IOException {
        stubFor(post(urlPathMatching("/subjects/" + topic + "-" + schema.getFullName()))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":" + schemaId + "}")));

        final SchemaString schemaString = new SchemaString(schema.toString());
        stubFor(get(urlPathMatching("/schemas/ids/" + schemaId))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody(schemaString.toJson())));
    }

    @Test
    void saveDiscountTest() throws Exception {
        // Given
        var discountDTO = ModelUtils.getDiscountDTO();

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/discount").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(discountDTO))).andExpect(status().isCreated());

        // Then
        Optional<DiscountEntity> optionalDiscount = discountJpaAdapter.findById(discountDTO.getCode());
        assertThat(optionalDiscount.isPresent()).isTrue();
        DiscountEntity discountEntity = optionalDiscount.get();
        assertThat(discountEntity.getDue()).isEqualTo(discountDTO.getDue());

        DiscountFactEvent discountFactEvent = DiscountFactEvent.newBuilder().setReason(EventReason.CREATE.name()).setCode(discountDTO.getCode()).setDue(discountDTO.getDue().toString()).build();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(testReceiver.factResult::get, c -> c.equals(discountFactEvent));
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(testReceiver.deltaResult::get, c -> c.equals(discountFactEvent));
    }
}
