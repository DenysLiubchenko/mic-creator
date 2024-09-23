package org.example.boot.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.example.boot.BootApplication;
import org.example.boot.ModelUtils;
import org.example.dao.adapter.DiscountJpaAdapter;
import org.example.dao.entity.DiscountEntity;
import org.example.domain.constant.EventReason;
import org.example.fact.DiscountFactEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

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
public class DiscountIT {
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DiscountJpaAdapter discountJpaAdapter;

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
    }
}
