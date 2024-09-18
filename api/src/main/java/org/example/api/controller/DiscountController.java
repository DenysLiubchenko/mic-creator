package org.example.api.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.example.api.generated.api.DiscountApi;
import org.example.api.generated.model.DiscountDTO;
import org.example.api.mapper.DiscountDtoMapper;
import org.example.domain.dto.DiscountDto;
import org.example.domain.service.DiscountService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Set;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class DiscountController implements DiscountApi {
    private final DiscountService discountService;
    private final DiscountDtoMapper discountDtoMapper;

    @Override
    public void saveDiscount(DiscountDTO discountDTO) {
        if (discountDTO.getDue().isBefore(OffsetDateTime.now())) {
            // TODO: maybe create own exception
            throw new ConstraintViolationException("Due date time must be in the future.", Set.of());
        }
        DiscountDto discount = discountDtoMapper.toDto(discountDTO);
        discountService.save(discount);
    }
}
