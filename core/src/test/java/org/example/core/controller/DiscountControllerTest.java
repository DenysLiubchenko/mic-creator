package org.example.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.ModelUtils;
import org.example.core.handler.CustomExceptionHandler;
import org.example.core.mapper.DiscountDtoMapper;
import org.example.domain.service.DiscountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiscountController.class)
@ContextConfiguration(classes = {DiscountController.class})
@Import(value = {CustomExceptionHandler.class})
class DiscountControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DiscountService discountService;
    @MockBean
    private DiscountDtoMapper discountDtoMapper;

    @Test
    void saveDiscountTest() throws Exception {
        // Given
        var discountDTO = ModelUtils.getDiscountDTO();
        var discountDto = ModelUtils.getDiscountDto();
        given(discountDtoMapper.toDto(discountDTO)).willReturn(discountDto);
        willDoNothing().given(discountService).save(discountDto);

        // When
        mockMvc.perform(post("/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(discountDTO))).andExpect(status().isCreated());

        // Then
        verify(discountService).save(discountDto);
        verify(discountDtoMapper).toDto(discountDTO);
    }

    @Test
    void saveDiscount_withDueDateInThePast_SendsBadRequestTest() throws Exception {
        // Given
        var discountDTO = ModelUtils.getDiscountDTO();
        discountDTO.due(OffsetDateTime.of(1970, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC));

        // When
        mockMvc.perform(post("/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(discountDTO))).andExpect(status().isBadRequest());
    }
}
