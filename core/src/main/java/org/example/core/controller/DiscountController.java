package org.example.core.controller;

import lombok.RequiredArgsConstructor;
import org.example.core.generated.api.DiscountApi;
import org.example.serviceapi.service.DiscountService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class DiscountController implements DiscountApi {
    private final DiscountService discountService;

    @Override
    public void saveDiscount(String code) {
        discountService.save(code);
    }
}
