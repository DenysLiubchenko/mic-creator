package org.example.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.api.ModelUtils;
import org.example.api.handler.CustomExceptionHandler;
import org.example.api.mapper.ProductDtoMapper;
import org.example.domain.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = {ProductController.class})
@Import(value = {AopAutoConfiguration.class, CustomExceptionHandler.class})
class ProductControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService productService;
    @MockBean
    private ProductDtoMapper productDtoMapper;

    @Test
    void saveProductTest() throws Exception {
        // Given
        var productDTO = ModelUtils.getProductDTO();
        var productDto = ModelUtils.getProductDto();
        given(productDtoMapper.toDto(productDTO)).willReturn(productDto);
        willDoNothing().given(productService).save(productDto);

        // When
        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated());

        // Then
        verify(productService).save(productDto);
        verify(productDtoMapper).toDto(productDTO);
    }

    @Test
    void saveProduct_withNegativeCost_sendsBadRequestTest() throws Exception {
        // Given
        var productDTO = ModelUtils.getProductDTO();
        productDTO.setCost(BigDecimal.valueOf(-299));

        // When
        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveProduct_withZeroCost_sendsBadRequestTest() throws Exception {
        // Given
        var productDTO = ModelUtils.getProductDTO();
        productDTO.setCost(BigDecimal.valueOf(-299));

        // When
        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveProduct_withEmptyName_sendsBadRequestTest() throws Exception {
        // Given
        var productDTO = ModelUtils.getProductDTO();
        productDTO.setName("");

        // When
        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveProduct_withBlankName_sendsBadRequestTest() throws Exception {
        // Given
        var productDTO = ModelUtils.getProductDTO();
        productDTO.setName("    ");

        // When
        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest());
    }
}
